package edu.lpnu.saas.repository.impl;

import edu.lpnu.saas.model.Organization;
import edu.lpnu.saas.repository.OrganizationRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrganizationRepository implements OrganizationRepository {

    private final Map<Long, Organization> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Organization save(Organization organization) {
        if (organization.getId() == null) {
            organization.setId(idGenerator.getAndIncrement());
            organization.setCreatedAt(Instant.now());
        }
        organization.setUpdatedAt(Instant.now());
        store.put(organization.getId(), organization);
        return organization;
    }

    @Override
    public Optional<Organization> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Organization> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}