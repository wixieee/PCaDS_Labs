package edu.lpnu.saas.repository.impl;

import edu.lpnu.saas.model.ActivityLog;
import edu.lpnu.saas.repository.ActivityLogRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryActivityLogRepository implements ActivityLogRepository {

    private final Map<Long, ActivityLog> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public ActivityLog save(ActivityLog log) {
        if (log.getId() == null) {
            log.setId(idGenerator.getAndIncrement());
            log.setCreatedAt(Instant.now());
        }
        log.setUpdatedAt(Instant.now());
        store.put(log.getId(), log);
        return log;
    }

    @Override
    public List<ActivityLog> findByOrganizationId(Long organizationId) {
        return store.values().stream()
                .filter(log -> organizationId.equals(log.getOrganizationId()))
                .collect(Collectors.toList());
    }
}