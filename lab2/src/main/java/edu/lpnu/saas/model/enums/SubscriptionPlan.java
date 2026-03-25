package edu.lpnu.saas.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPlan {
    FREE(null, 1,1000),
    STARTUP("price_1TEy8GFUf2LnAESzZI6wL57u",5, 10000),
    PRO("price_1TEy9RFUf2LnAESzwV7Dy1vv", 20, 100000);

    private final String stripePriceId;
    private final int maxCampaigns;
    private final int maxCommentsPerMonth;
}
