package edu.lpnu.saas.repository.impl;

import edu.lpnu.saas.model.Subscription;
import edu.lpnu.saas.model.enums.SubscriptionStatus;
import edu.lpnu.saas.repository.SubscriptionRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemorySubscriptionRepository implements SubscriptionRepository {

    private final Map<Long, Subscription> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Subscription save(Subscription subscription) {
        if (subscription.getId() == null) {
            subscription.setId(idGenerator.getAndIncrement());
            subscription.setCreatedAt(Instant.now());
        }
        subscription.setUpdatedAt(Instant.now());
        store.put(subscription.getId(), subscription);
        return subscription;
    }

    @Override
    public Optional<Subscription> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Subscription> findByOrganizationId(Long organizationId) {
        return store.values().stream()
                .filter(s -> s.getOrganizationId().equals(organizationId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Subscription> findByOrganizationIdAndStatus(Long organizationId, SubscriptionStatus status) {
        return store.values().stream()
                .filter(s -> s.getOrganizationId().equals(organizationId) && s.getStatus() == status)
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}