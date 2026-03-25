package edu.lpnu.saas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogResponse {
    private Long id;
    private Long userId;
    private Long organizationId;
    private String action;
    private String ipAddress;
    private Instant createdAt;
}
