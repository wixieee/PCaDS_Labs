package edu.lpnu.saas.analysis.service;

import edu.lpnu.saas.analysis.dto.ResourceLimitResponse;
import edu.lpnu.saas.analysis.exception.types.LimitExceededException;
import edu.lpnu.saas.analysis.model.ResourceLimit;
import edu.lpnu.saas.analysis.repository.ResourceLimitRepository;
import edu.lpnu.saas.analysis.util.mapper.ResourceLimitMapper;
import edu.lpnu.saas.common.dto.OrganizationCreatedEvent;
import edu.lpnu.saas.common.dto.OrganizationDeletedEvent;
import edu.lpnu.saas.common.dto.SubscriptionPlanChangedEvent;
import edu.lpnu.saas.common.dto.SubscriptionRenewedEvent;
import edu.lpnu.saas.common.exception.types.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceLimitService {

    private final ResourceLimitRepository resourceLimitRepository;
    private final ResourceLimitMapper mapper;

    @Transactional
    @RabbitListener(queues = "analysis.organization.created.queue")
    public void handleOrganizationCreated(OrganizationCreatedEvent event) {
        log.info("Організація {} створена. Ініціалізуємо базові ліміти.", event.getOrganizationId());
        ResourceLimit limit = ResourceLimit.builder()
                .organizationId(event.getOrganizationId())
                .maxComments(1000)
                .usedCommentsCount(0)
                .build();
        resourceLimitRepository.save(limit);
    }

    @Transactional
    @RabbitListener(queues = "analysis.organization.deleted.queue")
    public void handleOrganizationDeleted(OrganizationDeletedEvent event) {
        log.info("Організація {} видалена. Видаляємо її ліміти.", event.getOrganizationId());
        resourceLimitRepository.deleteByOrganizationId(event.getOrganizationId());
    }

    @Transactional
    @RabbitListener(queues = "analysis.subscription.plan.changed.queue")
    public void handleSubscriptionPlanChanged(SubscriptionPlanChangedEvent event) {
        log.info("Зміна тарифу для організації {}. Новий ліміт: {}", event.getOrganizationId(), event.getNewMaxComments());
        resourceLimitRepository.findByOrganizationId(event.getOrganizationId()).ifPresent(limit -> {
            limit.setMaxComments(event.getNewMaxComments());
            resourceLimitRepository.save(limit);
        });
    }

    @Transactional
    @RabbitListener(queues = "analysis.subscription.renewed.queue")
    public void handleSubscriptionRenewed(SubscriptionRenewedEvent event) {
        log.info("Початок нового білінг-циклу для організації {}. Обнуляємо використані ліміти.", event.getOrganizationId());
        resourceLimitRepository.findByOrganizationId(event.getOrganizationId()).ifPresent(limit -> {
            limit.setUsedCommentsCount(0);
            resourceLimitRepository.save(limit);
        });
    }

    @Transactional
    public void consumeComment(Long organizationId) {
        ResourceLimit limit = resourceLimitRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Ліміти для організації не знайдено"));

        if (limit.getUsedCommentsCount() >= limit.getMaxComments()) {
            throw new LimitExceededException(
                    "Перевищено місячний ліміт коментарів (" + limit.getMaxComments() + "). " +
                            "Будь ласка, перейдіть на вищий тарифний план."
            );
        }

        limit.setUsedCommentsCount(limit.getUsedCommentsCount() + 1);
        resourceLimitRepository.save(limit);
    }

    public ResourceLimitResponse getLimits(Long organizationId) {
        return mapper.toResponse(resourceLimitRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Ліміти для організації не знайдено")));
    }
}