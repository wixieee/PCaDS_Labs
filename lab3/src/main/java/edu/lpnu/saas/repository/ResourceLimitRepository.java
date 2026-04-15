package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.ResourceLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceLimitRepository extends JpaRepository<ResourceLimit, Long> {
    void deleteByOrganizationId(Long organizationId);
    Optional<ResourceLimit> findByOrganizationId(Long organizationId);
}