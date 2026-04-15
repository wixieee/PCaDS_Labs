package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    void deleteByOrganizationId(Long organizationId);
    Page<ActivityLog> findByOrganizationId(Long organizationId, Pageable pageable);
}