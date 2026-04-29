package edu.lpnu.saas.dto.request;

import edu.lpnu.saas.model.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePlanRequest {
    @NotNull(message = "План підписки не може бути порожнім")
    private SubscriptionPlan plan;
}