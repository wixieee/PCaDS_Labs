package edu.lpnu.saas.controller;

import edu.lpnu.saas.dto.request.InviteMemberRequest;
import edu.lpnu.saas.dto.request.UpdateRoleRequest;
import edu.lpnu.saas.security.JwtPrincipal;
import edu.lpnu.saas.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations/{organizationId}/members")
@RequiredArgsConstructor
@Tag(name = "Memberships")
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping
    @Operation(summary = "Запросити користувача в організацію")
    public ResponseEntity<@NonNull Void> invite(
            @PathVariable Long organizationId,
            @Valid @RequestBody InviteMemberRequest request,
            Authentication authentication
    ) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        membershipService.inviteMember(organizationId, request, principal.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/role")
    @Operation(summary = "Оновити роль користувача")
    public ResponseEntity<@NonNull Void> updateRole(
            @PathVariable Long organizationId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRoleRequest request,
            Authentication authentication
    ) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        membershipService.updateMemberRole(organizationId, userId, request, principal.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Видалити користувача з організації")
    public ResponseEntity<@NonNull Void> remove(
            @PathVariable Long organizationId,
            @PathVariable Long userId,
            Authentication authentication
    ) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        membershipService.removeMember(organizationId, userId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}