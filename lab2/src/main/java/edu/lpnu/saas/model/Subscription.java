package edu.lpnu.saas.model;

import edu.lpnu.saas.model.enums.SubscriptionPlan;
import edu.lpnu.saas.model.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Subscription extends BaseEntity{
    private Long organizationId;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;
    private Instant startTime;
    private Instant endTime;
}
