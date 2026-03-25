package edu.lpnu.saas.controller;

import edu.lpnu.saas.dto.request.ChangePlanRequest;
import edu.lpnu.saas.dto.response.SubscriptionResponse;
import edu.lpnu.saas.service.PaymentService;
import edu.lpnu.saas.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/organizations/{organizationId}/subscription")
@RequiredArgsConstructor
@Tag(name = "Subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService; // Додали PaymentService

    @GetMapping
    @Operation(summary = "Отримати поточну підписку організації")
    public ResponseEntity<@NonNull SubscriptionResponse> getCurrent(@PathVariable Long organizationId) {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(organizationId));
    }

    @PostMapping("/checkout")
    @Operation(summary = "Отримати посилання на оплату для переходу на новий тариф")
    public ResponseEntity<@NonNull Map<String, String>> createCheckout(
            @PathVariable Long organizationId,
            @Valid @RequestBody ChangePlanRequest request
    ) {
        String checkoutUrl = paymentService.createCheckoutSession(organizationId, request.getPlan());
        return ResponseEntity.ok(Map.of("checkoutUrl", checkoutUrl));
    }

    @PostMapping("/cancel")
    @Operation(summary = "Скасувати поточну підписку")
    public ResponseEntity<@NonNull SubscriptionResponse> cancel(@PathVariable Long organizationId) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(organizationId));
    }
}