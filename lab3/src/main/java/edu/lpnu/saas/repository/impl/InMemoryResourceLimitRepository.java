package edu.lpnu.saas.repository.impl;

import edu.lpnu.saas.model.ResourceLimit;
import edu.lpnu.saas.repository.ResourceLimitRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryResourceLimitRepository implements ResourceLimitRepository {

    private final Map<Long, ResourceLimit> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public ResourceLimit save(ResourceLimit resourceLimit) {
        if (resourceLimit.getId() == null) {
            resourceLimit.setId(idGenerator.getAndIncrement());
            resourceLimit.setCreatedAt(Instant.now());
        }
        resourceLimit.setUpdatedAt(Instant.now());
        store.put(resourceLimit.getId(), resourceLimit);
        return resourceLimit;
    }

    @Override
    public Optional<ResourceLimit> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<ResourceLimit> findByOrganizationId(Long organizationId) {
        return store.values().stream()
                .filter(limit -> limit.getOrganizationId().equals(organizationId))
                .findFirst();
    }

    @Override
    public List<ResourceLimit> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}