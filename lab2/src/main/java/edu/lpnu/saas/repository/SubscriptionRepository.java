package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.Subscription;
import edu.lpnu.saas.model.enums.SubscriptionStatus;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    Optional<Subscription> findById(Long id);
    List<Subscription> findByOrganizationId(Long organizationId);
    Optional<Subscription> findByOrganizationIdAndStatus(Long organizationId, SubscriptionStatus status);
    void deleteById(Long id);
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}