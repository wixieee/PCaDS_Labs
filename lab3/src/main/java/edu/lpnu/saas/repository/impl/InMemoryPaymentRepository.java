package edu.lpnu.saas.repository.impl;

import edu.lpnu.saas.model.Payment;
import edu.lpnu.saas.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<Long, Payment> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(idGenerator.getAndIncrement());
            payment.setCreatedAt(Instant.now());
        }
        payment.setUpdatedAt(Instant.now());
        store.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Payment> findByOrganizationId(Long organizationId) {
        return store.values().stream()
                .filter(p -> p.getOrganizationId().equals(organizationId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Payment> findByStripeSessionId(String sessionId) {
        return store.values().stream()
                .filter(p -> sessionId.equals(p.getStripeSessionId()))
                .findFirst();
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}