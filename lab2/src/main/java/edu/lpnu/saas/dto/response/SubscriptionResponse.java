package edu.lpnu.saas.dto.response;

import edu.lpnu.saas.model.enums.SubscriptionPlan;
import edu.lpnu.saas.model.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private Long id;
    private Long organizationId;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;
    private Instant startTime;
    private Instant endTime;
}