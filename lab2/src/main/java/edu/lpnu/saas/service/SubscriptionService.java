package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.request.ChangePlanRequest;
import edu.lpnu.saas.dto.response.SubscriptionResponse;
import edu.lpnu.saas.exception.types.NotFoundException;
import edu.lpnu.saas.model.ResourceLimit;
import edu.lpnu.saas.model.Subscription;
import edu.lpnu.saas.model.enums.SubscriptionStatus;
import edu.lpnu.saas.repository.ResourceLimitRepository;
import edu.lpnu.saas.repository.SubscriptionRepository;
import edu.lpnu.saas.util.mapper.SubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ResourceLimitRepository resourceLimitRepository;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionResponse getCurrentSubscription(Long organizationId) {
        Subscription subscription = findActiveSubscription(organizationId);
        return subscriptionMapper.toSubscriptionResponse(subscription);
    }

    public SubscriptionResponse changePlan(Long organizationId, ChangePlanRequest request) {
        Subscription subscription = findActiveSubscription(organizationId);

        if (subscription.getPlan() == request.getPlan()) {
            throw new IllegalArgumentException("Організація вже використовує цей план підписки");
        }

        subscription.setPlan(request.getPlan());
        subscription.setStartTime(Instant.now());
        subscription.setEndTime(Instant.now().plus(30, ChronoUnit.DAYS));
        subscription = subscriptionRepository.save(subscription);

        ResourceLimit limit = resourceLimitRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Ліміти ресурсів не знайдено"));

        limit.setMaxCampaigns(request.getPlan().getMaxCampaigns());
        limit.setMaxComments(request.getPlan().getMaxCommentsPerMonth());
        resourceLimitRepository.save(limit);

        return subscriptionMapper.toSubscriptionResponse(subscription);
    }

    public SubscriptionResponse cancelSubscription(Long organizationId) {
        Subscription subscription = findActiveSubscription(organizationId);

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toSubscriptionResponse(subscription);
    }

    private Subscription findActiveSubscription(Long organizationId) {
        return subscriptionRepository.findByOrganizationIdAndStatus(organizationId, SubscriptionStatus.ACTIVE)
                .orElseGet(() -> subscriptionRepository.findByOrganizationIdAndStatus(organizationId, SubscriptionStatus.CANCELED)
                        .orElseThrow(() -> new NotFoundException("Підписку не знайдено")));
    }
}