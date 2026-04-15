package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.response.SubscriptionResponse;
import edu.lpnu.saas.exception.types.NotFoundException;
import edu.lpnu.saas.model.ResourceLimit;
import edu.lpnu.saas.model.Subscription;
import edu.lpnu.saas.model.enums.SubscriptionPlan;
import edu.lpnu.saas.model.enums.SubscriptionStatus;
import edu.lpnu.saas.repository.ResourceLimitRepository;
import edu.lpnu.saas.repository.SubscriptionRepository;
import edu.lpnu.saas.util.mapper.SubscriptionMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final ResourceLimitRepository resourceLimitRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final PaymentService paymentService;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            ResourceLimitRepository resourceLimitRepository,
            SubscriptionMapper subscriptionMapper,
            @Lazy PaymentService paymentService
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.resourceLimitRepository = resourceLimitRepository;
        this.subscriptionMapper = subscriptionMapper;
        this.paymentService = paymentService;
    }

    public SubscriptionResponse getCurrentSubscription(Long organizationId) {
        Subscription subscription = findActiveSubscription(organizationId);
        return subscriptionMapper.toSubscriptionResponse(subscription);
    }

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

        ResourceLimit limit = resourceLimitRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Ліміти ресурсів не знайдено"));

        limit.setMaxComments(newPlan.getMaxCommentsPerMonth());
        resourceLimitRepository.save(limit);
    }

    public void renewSubscriptionByStripeId(String stripeSubscriptionId) {
        Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId)
                .orElseThrow(() -> new NotFoundException("Підписку з ID не знайдено"));

        Instant newEndTime = subscription.getEndTime().isAfter(Instant.now())
                ? subscription.getEndTime().plus(30, ChronoUnit.DAYS)
                : Instant.now().plus(30, ChronoUnit.DAYS);

        subscription.setEndTime(newEndTime);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(subscription);

        ResourceLimit limit = resourceLimitRepository.findByOrganizationId(subscription.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Ліміти ресурсів не знайдено"));

        limit.setUsedCommentsCount(0);
        resourceLimitRepository.save(limit);
    }

    public SubscriptionResponse cancelSubscription(Long organizationId) {
        Subscription subscription = findActiveSubscription(organizationId);

        if (subscription.getStripeSubscriptionId() != null && !subscription.getStripeSubscriptionId().isEmpty()) {
            paymentService.cancelStripeSubscription(subscription.getStripeSubscriptionId());
        }

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toSubscriptionResponse(subscription);
    }

    public void handleFailedRenewal(String stripeSubscriptionId) {
        subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId).ifPresent(subscription -> {
            subscription.setPlan(SubscriptionPlan.FREE);
            subscription.setStatus(SubscriptionStatus.ACTIVE);

            subscriptionRepository.save(subscription);

            ResourceLimit limit = resourceLimitRepository.findByOrganizationId(subscription.getOrganizationId())
                    .orElseThrow(() -> new NotFoundException("Ліміти ресурсів не знайдено"));

            limit.setMaxComments(SubscriptionPlan.FREE.getMaxCommentsPerMonth());
            resourceLimitRepository.save(limit);
        });
    }

    private Subscription findActiveSubscription(Long organizationId) {
        return subscriptionRepository.findByOrganizationIdAndStatus(organizationId, SubscriptionStatus.ACTIVE)
                .orElseGet(() -> subscriptionRepository.findByOrganizationIdAndStatus(organizationId, SubscriptionStatus.CANCELED)
                        .orElseThrow(() -> new NotFoundException("Підписку не знайдено")));
    }
}