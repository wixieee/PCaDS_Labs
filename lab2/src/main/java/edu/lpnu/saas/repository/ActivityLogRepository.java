package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.ActivityLog;
import java.util.List;

public interface ActivityLogRepository {
    ActivityLog save(ActivityLog activityLog);
    List<ActivityLog> findByOrganizationId(Long organizationId);
    List<ActivityLog> findByUserId(Long userId);
    List<ActivityLog> findAll();
}