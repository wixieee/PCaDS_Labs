package edu.lpnu.saas.analysis.controller;

import edu.lpnu.saas.analysis.api.ResourceLimitsApi;
import edu.lpnu.saas.analysis.dto.ResourceLimitResponse;
import edu.lpnu.saas.analysis.service.ResourceLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResourceLimitController implements ResourceLimitsApi {

    private final ResourceLimitService resourceLimitService;

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'VIEWER')")
    public ResponseEntity<ResourceLimitResponse> getLimits(Long organizationId) {
        return ResponseEntity.ok(resourceLimitService.getLimits(organizationId));
    }
}