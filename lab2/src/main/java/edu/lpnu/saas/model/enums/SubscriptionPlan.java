package edu.lpnu.saas.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPlan {
    FREE(BigDecimal.ZERO, 1,1000),
    STARTUP(new BigDecimal("99.99"),5, 10000),
    PRO(new BigDecimal("299.99"), 20, 100000);

    private final BigDecimal price;
    private final int maxCampaigns;
    private final int maxCommentsPerMonth;
}
