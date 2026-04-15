package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.ActivityLog;

import java.util.List;

public interface ActivityLogRepository {
    ActivityLog save(ActivityLog log);
    List<ActivityLog> findByOrganizationId(Long organizationId);
}