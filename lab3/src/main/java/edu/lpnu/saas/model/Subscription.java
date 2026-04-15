package edu.lpnu.saas.model;

import edu.lpnu.saas.model.enums.SubscriptionPlan;
import edu.lpnu.saas.model.enums.SubscriptionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends BaseEntity {
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private String stripeSubscriptionId;
    private Instant startTime;
    private Instant endTime;
}