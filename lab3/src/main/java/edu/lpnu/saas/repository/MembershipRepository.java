package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByUserId(Long userId);
    void deleteByOrganizationId(Long organizationId);
    Optional<Membership> findByOrganizationIdAndUserId(Long organizationId, Long userId);
}