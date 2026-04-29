package edu.lpnu.saas.util.scheduler;

import edu.lpnu.saas.model.Subscription;
import edu.lpnu.saas.model.enums.SubscriptionPlan;
import edu.lpnu.saas.model.enums.SubscriptionStatus;
import edu.lpnu.saas.repository.ResourceLimitRepository;
import edu.lpnu.saas.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final ResourceLimitRepository resourceLimitRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processExpiredCanceledSubscriptions() {
        log.debug("Запуск перевірки протермінованих скасованих підписок...");

        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findByStatusAndEndTimeBefore(SubscriptionStatus.CANCELED, Instant.now());

        for (Subscription sub : expiredSubscriptions) {
            sub.setPlan(SubscriptionPlan.FREE);
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setEndTime(null);
            sub.setStripeSubscriptionId(null);
            subscriptionRepository.save(sub);

            resourceLimitRepository.findByOrganizationId(sub.getOrganizationId()).ifPresent(limit -> {
                limit.setMaxComments(SubscriptionPlan.FREE.getMaxCommentsPerMonth());
                resourceLimitRepository.save(limit);
            });

            log.debug("Організація {} успішно переведена на FREE план після закінчення скасованої підписки.", sub.getOrganizationId());
        }
    }
}