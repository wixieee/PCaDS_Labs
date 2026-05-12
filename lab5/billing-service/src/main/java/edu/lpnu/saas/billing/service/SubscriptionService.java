package edu.lpnu.saas.billing.service;

import com.stripe.exception.StripeException;
import edu.lpnu.saas.billing.dto.SubscriptionResponse;
import edu.lpnu.saas.billing.exception.types.PaymentException;
import edu.lpnu.saas.billing.model.Subscription;
import edu.lpnu.saas.billing.model.enums.SubscriptionPlan;
import edu.lpnu.saas.billing.model.enums.SubscriptionStatus;
import edu.lpnu.saas.billing.repository.SubscriptionRepository;
import edu.lpnu.saas.billing.util.mapper.SubscriptionMapper;
import edu.lpnu.saas.common.dto.OrganizationCreatedEvent;
import edu.lpnu.saas.common.dto.OrganizationDeletedEvent;
import edu.lpnu.saas.common.dto.SubscriptionPlanChangedEvent;
import edu.lpnu.saas.common.dto.SubscriptionRenewedEvent;
import edu.lpnu.saas.common.exception.types.BadRequestException;
import edu.lpnu.saas.common.exception.types.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            SubscriptionMapper subscriptionMapper,
            @Lazy PaymentService paymentService,
            RabbitTemplate rabbitTemplate
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionMapper = subscriptionMapper;
        this.paymentService = paymentService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    @RabbitListener(queues = "billing.organization.created.queue")
    public void handleOrganizationCreated(OrganizationCreatedEvent event) {
        log.info("Отримано подію створення організації {}. Створюємо FREE підписку.", event.getOrganizationId());
        Subscription subscription = Subscription.builder()
                .organizationId(event.getOrganizationId())
                .plan(SubscriptionPlan.FREE)
                .status(SubscriptionStatus.ACTIVE)
                .startTime(Instant.now())
                .endTime(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();
        subscriptionRepository.save(subscription);

        publishPlanChangedEvent(subscription);
    }

    @Transactional
    @RabbitListener(queues = "billing.organization.deleted.queue")
    public void handleOrganizationDeleted(OrganizationDeletedEvent event) {
        log.info("Отримано подію видалення організації {}. Скасовуємо підписку в Stripe і чистимо БД.", event.getOrganizationId());

        subscriptionRepository.findByOrganizationIdAndStatus(event.getOrganizationId(), SubscriptionStatus.ACTIVE)
                .ifPresent(sub -> {
                    if (sub.getStripeSubscriptionId() != null) {
                        try {
                            paymentService.cancelStripeSubscription(sub.getStripeSubscriptionId());
                        } catch (Exception e) {
                            log.error("Не вдалося скасувати підписку в Stripe для організації {}: {}", event.getOrganizationId(), e.getMessage());
                            throw new RuntimeException("Помилка при скасуванні підписки в Stripe", e);
                        }
                    }
                });

        subscriptionRepository.deleteByOrganizationId(event.getOrganizationId());
        paymentService.deletePaymentsByOrganizationId(event.getOrganizationId());
    }

    public SubscriptionResponse getCurrentSubscription(Long organizationId) {
        Subscription subscription = findActiveSubscription(organizationId);
        return subscriptionMapper.toSubscriptionResponse(subscription);
    }

    @Transactional
    public void activatePlan(Long organizationId, SubscriptionPlan newPlan, String stripeSubscriptionId) {
        Subscription subscription = findActiveSubscription(organizationId);

        if (subscription.getPlan() == newPlan) {
            if (stripeSubscriptionId != null) {
                subscription.setStripeSubscriptionId(stripeSubscriptionId);
                subscriptionRepository.save(subscription);
            }
            return;
        }

        subscription.setPlan(newPlan);
        subscription.setStripeSubscriptionId(stripeSubscriptionId);
        subscription.setStartTime(Instant.now());
        subscription.setEndTime(Instant.now().plus(30, ChronoUnit.DAYS));
        subscriptionRepository.save(subscription);

        publishPlanChangedEvent(subscription);
        log.info("План для організації {} змінено на {}", organizationId, newPlan);
    }

    @Transactional
    public void renewSubscriptionByStripeId(String stripeSubscriptionId) {
        Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId)
                .orElseThrow(() -> new NotFoundException("Підписку з ID не знайдено"));

        Instant newEndTime = subscription.getEndTime() != null && subscription.getEndTime().isAfter(Instant.now())
                ? subscription.getEndTime().plus(30, ChronoUnit.DAYS)
                : Instant.now().plus(30, ChronoUnit.DAYS);

        subscription.setEndTime(newEndTime);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(subscription);

        publishRenewedEvent(subscription);
    }

    @Transactional
    public SubscriptionResponse cancelSubscription(Long organizationId) {
        Subscription subscription = findActiveSubscription(organizationId);

        if (subscription.getPlan() == SubscriptionPlan.FREE) {
            throw new BadRequestException("Безкоштовний тариф не можна скасувати, оскільки це базовий план.");
        }

        if (subscription.getStripeSubscriptionId() != null && !subscription.getStripeSubscriptionId().isEmpty()) {
            try {
                paymentService.cancelStripeSubscription(subscription.getStripeSubscriptionId());
            } catch (StripeException e) {
                log.error("Помилка Stripe при скасуванні: {}", e.getMessage());
                throw new PaymentException("Не вдалося скасувати підписку в платіжній системі");
            }
        }

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toSubscriptionResponse(subscription);
    }

    @Transactional
    public void handleFailedRenewal(String stripeSubscriptionId) {
        subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId).ifPresent(subscription -> {
            subscription.setPlan(SubscriptionPlan.FREE);
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStripeSubscriptionId(null);
            subscription.setEndTime(Instant.now().plus(30, ChronoUnit.DAYS)); // Починається новий FREE цикл

            subscriptionRepository.save(subscription);

            publishPlanChangedEvent(subscription);
            log.warn("Не вдалося продовжити підписку {}. Організацію переведено на FREE.", stripeSubscriptionId);
        });
    }

    private Subscription findActiveSubscription(Long organizationId) {
        return subscriptionRepository.findByOrganizationIdAndStatus(organizationId, SubscriptionStatus.ACTIVE)
                .orElseGet(() -> subscriptionRepository.findByOrganizationIdAndStatus(organizationId, SubscriptionStatus.CANCELED)
                        .orElseThrow(() -> new NotFoundException("Підписку не знайдено")));
    }

    private void publishPlanChangedEvent(Subscription subscription) {
        SubscriptionPlanChangedEvent event = SubscriptionPlanChangedEvent.builder()
                .organizationId(subscription.getOrganizationId())
                .newPlan(subscription.getPlan().name())
                .newMaxComments(subscription.getPlan().getMaxCommentsPerMonth())
                .build();
        rabbitTemplate.convertAndSend("billing.exchange", "subscription.plan.changed", event);
    }

    private void publishRenewedEvent(Subscription subscription) {
        SubscriptionRenewedEvent event = SubscriptionRenewedEvent.builder()
                .organizationId(subscription.getOrganizationId())
                .build();
        rabbitTemplate.convertAndSend("billing.exchange", "subscription.renewed", event);
    }
}