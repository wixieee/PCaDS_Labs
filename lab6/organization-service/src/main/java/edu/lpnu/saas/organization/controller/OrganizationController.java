package edu.lpnu.saas.organization.controller;

import edu.lpnu.saas.common.aop.AuditAction;
import edu.lpnu.saas.common.security.JwtPrincipal;
import edu.lpnu.saas.organization.api.OrganizationsApi;
import edu.lpnu.saas.organization.dto.OrganizationRequest;
import edu.lpnu.saas.organization.dto.OrganizationResponse;
import edu.lpnu.saas.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrganizationController implements OrganizationsApi {

    private final OrganizationService organizationService;

    @Override
    public ResponseEntity<OrganizationResponse> create(OrganizationRequest request) {
        OrganizationResponse response = organizationService.createOrganization(request, JwtPrincipal.getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<List<OrganizationResponse>> getAllMyOrganizations() {
        return ResponseEntity.ok(organizationService.getUserOrganizations(JwtPrincipal.getCurrentUserId()));
    }

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#id, 'VIEWER')")
    public ResponseEntity<OrganizationResponse> getById(Long id) {
        return ResponseEntity.ok(organizationService.getOrganizationById(id));
    }

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#id, 'OWNER')")
    @AuditAction(action = "ORGANIZATION_UPDATE", orgId = "#id")
    public ResponseEntity<OrganizationResponse> update(Long id, OrganizationRequest request) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, request));
    }

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#id, 'OWNER')")
    @AuditAction(action = "ORGANIZATION_DELETE", orgId = "#id")
    public ResponseEntity<Void> delete(Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
}