package edu.lpnu.saas.auth.controller;

import edu.lpnu.saas.auth.api.OrganizationsApi;
import edu.lpnu.saas.auth.dto.InviteMemberRequest;
import edu.lpnu.saas.auth.dto.UpdateRoleRequest;
import edu.lpnu.saas.auth.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import static edu.lpnu.saas.common.security.JwtPrincipal.getCurrentUserId;

@RestController
@RequiredArgsConstructor
public class MembershipController implements OrganizationsApi {

    private final MembershipService membershipService;

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'ADMIN')")
    public ResponseEntity<Void> invite(Long organizationId, InviteMemberRequest request) {
        membershipService.inviteMember(organizationId, request, getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'ADMIN')")
    public ResponseEntity<Void> remove(Long organizationId, Long userId) {
        membershipService.removeMember(organizationId, userId, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'ADMIN')")
    public ResponseEntity<Void> updateRole(Long organizationId, Long userId, UpdateRoleRequest request) {
        membershipService.updateMemberRole(organizationId, userId, request, getCurrentUserId());
        return ResponseEntity.ok().build();
    }
}