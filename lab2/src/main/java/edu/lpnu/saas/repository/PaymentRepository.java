package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findByOrganizationId(Long organizationId);
    List<Payment> findAll();
    Optional<Payment> findByStripeSessionId(String sessionId);
    void deleteById(Long id);
}