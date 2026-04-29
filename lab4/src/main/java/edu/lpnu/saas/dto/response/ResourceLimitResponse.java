package edu.lpnu.saas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceLimitResponse {
    private Long organizationId;
    private Integer maxComments;
    private Integer usedCommentsCount;
}
