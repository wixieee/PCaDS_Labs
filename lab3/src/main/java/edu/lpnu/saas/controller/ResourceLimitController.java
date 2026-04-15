package edu.lpnu.saas.controller;

import edu.lpnu.saas.dto.response.ResourceLimitResponse;
import edu.lpnu.saas.service.ResourceLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations/{organizationId}/limits")
@RequiredArgsConstructor
@Tag(name = "Resource Limits")
public class ResourceLimitController {

    private final ResourceLimitService resourceLimitService;

    @GetMapping
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'VIEWER')")
    @Operation(summary = "Отримати поточні ліміти та їх використання для організації")
    public ResponseEntity<@NonNull ResourceLimitResponse> getLimits(@PathVariable Long organizationId) {
        return ResponseEntity.ok(resourceLimitService.getLimits(organizationId));
    }
}