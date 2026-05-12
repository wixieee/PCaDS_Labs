package edu.lpnu.saas.billing.util.scheduler;

import edu.lpnu.saas.billing.model.Subscription;
import edu.lpnu.saas.billing.model.enums.SubscriptionPlan;
import edu.lpnu.saas.billing.model.enums.SubscriptionStatus;
import edu.lpnu.saas.billing.repository.SubscriptionRepository;
import edu.lpnu.saas.common.dto.SubscriptionPlanChangedEvent;
import edu.lpnu.saas.common.dto.SubscriptionRenewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void renewFreeSubscriptions() {
        log.info("Перевірка активних FREE підписок для поновлення циклу...");

        List<Subscription> expiredFreeSubscriptions = subscriptionRepository
                .findByStatusAndEndTimeBefore(SubscriptionStatus.ACTIVE, Instant.now())
                .stream()
                .filter(sub -> sub.getPlan() == SubscriptionPlan.FREE)
                .toList();

        for (Subscription sub : expiredFreeSubscriptions) {
            sub.setEndTime(Instant.now().plus(30, ChronoUnit.DAYS));
            subscriptionRepository.save(sub);

            SubscriptionRenewedEvent event = new SubscriptionRenewedEvent(sub.getOrganizationId());
            rabbitTemplate.convertAndSend("billing.exchange", "subscription.renewed", event);
        }

        if (!expiredFreeSubscriptions.isEmpty()) {
            log.info("Оброблено {} поновлень для FREE підписок.", expiredFreeSubscriptions.size());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkExpiredCanceledSubscriptions() {
        log.info("Перевірка протермінованих скасованих підписок...");

        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findByStatusAndEndTimeBefore(SubscriptionStatus.CANCELED, Instant.now());

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setPlan(SubscriptionPlan.FREE);
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStripeSubscriptionId(null);
            subscription.setEndTime(Instant.now().plus(30, ChronoUnit.DAYS));

            subscriptionRepository.save(subscription);

            SubscriptionPlanChangedEvent event = SubscriptionPlanChangedEvent.builder()
                    .organizationId(subscription.getOrganizationId())
                    .newPlan(SubscriptionPlan.FREE.name())
                    .newMaxComments(SubscriptionPlan.FREE.getMaxCommentsPerMonth())
                    .build();
            rabbitTemplate.convertAndSend("billing.exchange", "subscription.plan.changed", event);

            log.info("Підписка організації {} завершилась. Повернуто план FREE.", subscription.getOrganizationId());
        }
    }
}