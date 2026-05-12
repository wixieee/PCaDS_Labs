package edu.lpnu.saas.organization.repository;

import edu.lpnu.saas.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}