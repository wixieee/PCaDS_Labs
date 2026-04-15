package edu.lpnu.saas.controller;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Invoice;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import edu.lpnu.saas.service.PaymentService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Hidden
public class WebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<@NonNull String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        switch (event.getType()) {
            case "checkout.session.completed":
                if (dataObjectDeserializer.getObject().isPresent()) {
                    Session session = (Session) dataObjectDeserializer.getObject().get();
                    paymentService.handleCheckoutSessionCompleted(session);
                }
                break;

            case "checkout.session.expired":
                if (dataObjectDeserializer.getObject().isPresent()) {
                    Session session = (Session) dataObjectDeserializer.getObject().get();
                    paymentService.handleCheckoutSessionExpired(session);
                }
                break;

            case "invoice.paid":
                if (dataObjectDeserializer.getObject().isPresent()) {
                    Invoice invoice = (Invoice) dataObjectDeserializer.getObject().get();
                    paymentService.handleInvoicePaid(invoice);
                }
                break;

            case "invoice.payment_failed":
                if (dataObjectDeserializer.getObject().isPresent()) {
                    Invoice invoice = (Invoice) dataObjectDeserializer.getObject().get();
                    paymentService.handleInvoicePaymentFailed(invoice);
                }
                break;

            default:
                break;
        }

        return ResponseEntity.ok("Успіх");
    }
}