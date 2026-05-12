package edu.lpnu.saas.billing.repository;

import edu.lpnu.saas.billing.model.Subscription;
import edu.lpnu.saas.billing.model.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    void deleteByOrganizationId(Long organizationId);
    Optional<Subscription> findByOrganizationIdAndStatus(Long organizationId, SubscriptionStatus status);
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
    List<Subscription> findByStatusAndEndTimeBefore(SubscriptionStatus status, Instant time);
}