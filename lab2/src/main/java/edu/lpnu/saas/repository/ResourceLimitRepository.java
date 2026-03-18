package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.ResourceLimit;
import java.util.List;
import java.util.Optional;

public interface ResourceLimitRepository {
    ResourceLimit save(ResourceLimit resourceLimit);
    Optional<ResourceLimit> findById(Long id);
    Optional<ResourceLimit> findByOrganizationId(Long organizationId);
    List<ResourceLimit> findAll();
    void deleteById(Long id);
}