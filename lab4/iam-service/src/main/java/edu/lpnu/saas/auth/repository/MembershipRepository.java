package edu.lpnu.saas.auth.repository;

import edu.lpnu.saas.auth.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByUserId(Long userId);
    Optional<Membership> findByOrganizationIdAndUserId(Long organizationId, Long userId);
    void deleteByOrganizationIdAndUserId(Long organizationId, Long userId);
}