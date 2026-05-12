package edu.lpnu.saas.billing.controller;

import edu.lpnu.saas.billing.api.SubscriptionsApi;
import edu.lpnu.saas.billing.dto.ChangePlanRequest;
import edu.lpnu.saas.billing.dto.SubscriptionResponse;
import edu.lpnu.saas.billing.model.enums.SubscriptionPlan;
import edu.lpnu.saas.billing.service.PaymentService;
import edu.lpnu.saas.billing.service.SubscriptionService;
import edu.lpnu.saas.common.aop.AuditAction;
import edu.lpnu.saas.common.security.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SubscriptionController implements SubscriptionsApi {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'VIEWER')")
    public ResponseEntity<SubscriptionResponse> getCurrent(Long organizationId) {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(organizationId));
    }

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'OWNER')")
    @AuditAction(action = "SUBSCRIPTION_CHECKOUT", orgId = "#organizationId")
    public ResponseEntity<Map<String, String>> createCheckout(Long organizationId, ChangePlanRequest request) {
        SubscriptionPlan plan = SubscriptionPlan.valueOf(request.getPlan().name());
        String userEmail = JwtPrincipal.getCurrentUserEmail();

        String checkoutUrl = paymentService.createCheckoutSession(organizationId, userEmail, plan);
        return ResponseEntity.ok(Map.of("url", checkoutUrl));
    }

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'OWNER')")
    @AuditAction(action = "SUBSCRIPTION_CANCEL", orgId = "#organizationId")
    public ResponseEntity<SubscriptionResponse> cancel(Long organizationId) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(organizationId));
    }
}