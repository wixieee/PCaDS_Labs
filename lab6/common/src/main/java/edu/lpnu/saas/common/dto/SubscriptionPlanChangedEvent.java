package edu.lpnu.saas.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanChangedEvent {
    private Long organizationId;
    private String newPlan;
    private Integer newMaxComments;
}