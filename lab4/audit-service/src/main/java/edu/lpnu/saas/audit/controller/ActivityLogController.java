package edu.lpnu.saas.audit.controller;

import edu.lpnu.saas.audit.api.AuditLogsApi;
import edu.lpnu.saas.audit.dto.PageActivityLogResponse;
import edu.lpnu.saas.audit.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActivityLogController implements AuditLogsApi {

    private final ActivityLogService activityLogService;

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'ADMIN')")
    public ResponseEntity<PageActivityLogResponse> getAuditLogs(Long organizationId, Integer page, Integer size) {

        PageActivityLogResponse response = activityLogService.getOrganizationLogs(
                organizationId,
                page != null ? page : 0,
                size != null ? size : 10
        );

        return ResponseEntity.ok(response);
    }
}