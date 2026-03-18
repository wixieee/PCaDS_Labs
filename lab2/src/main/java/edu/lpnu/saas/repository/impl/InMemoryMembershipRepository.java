package edu.lpnu.saas.repository.impl;

import edu.lpnu.saas.model.Membership;
import edu.lpnu.saas.repository.MembershipRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryMembershipRepository implements MembershipRepository {

    private final Map<Long, Membership> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Membership save(Membership membership) {
        if (membership.getId() == null) {
            membership.setId(idGenerator.getAndIncrement());
            membership.setCreatedAt(Instant.now());
        }
        membership.setUpdatedAt(Instant.now());
        store.put(membership.getId(), membership);
        return membership;
    }

    @Override
    public Optional<Membership> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Membership> findByUserId(Long userId) {
        return store.values().stream()
                .filter(m -> m.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByOrganizationId(Long organizationId) {
        return store.values().stream()
                .filter(m -> m.getOrganizationId().equals(organizationId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Membership> findByOrganizationIdAndUserId(Long organizationId, Long userId) {
        return store.values().stream()
                .filter(m -> m.getOrganizationId().equals(organizationId) && m.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}