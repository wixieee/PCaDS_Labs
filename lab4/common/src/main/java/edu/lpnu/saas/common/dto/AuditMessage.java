package edu.lpnu.saas.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditMessage {
    private Long userId;
    private Long organizationId;
    private String action;
    private String ipAddress;
}