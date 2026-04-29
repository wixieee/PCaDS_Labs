package edu.lpnu.saas.controller;

import edu.lpnu.saas.dto.response.ActivityLogResponse;
import edu.lpnu.saas.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations/{organizationId}/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'ADMIN')")
    @Operation(summary = "Отримати журнал дій організації")
    public ResponseEntity<@NonNull Page<ActivityLogResponse>> getAuditLogs(
            @PathVariable Long organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(activityLogService.getOrganizationLogs(organizationId, page, size));
    }
}