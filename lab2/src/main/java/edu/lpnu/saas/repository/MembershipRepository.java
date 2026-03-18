package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.Membership;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository {
    Membership save(Membership membership);
    Optional<Membership> findById(Long id);
    List<Membership> findByUserId(Long userId);
    List<Membership> findByOrganizationId(Long organizationId);
    Optional<Membership> findByOrganizationIdAndUserId(Long organizationId, Long userId);
    void deleteById(Long id);
}