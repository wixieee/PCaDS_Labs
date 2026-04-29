package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    void deleteByOrganizationId(Long organizationId);
    Optional<Payment> findByStripeSessionId(String sessionId);
}