package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}