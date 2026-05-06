package edu.lpnu.saas.billing.service;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import edu.lpnu.saas.billing.exception.types.PaymentException;
import edu.lpnu.saas.common.exception.types.NotFoundException;
import edu.lpnu.saas.billing.model.Payment;
import edu.lpnu.saas.billing.model.enums.PaymentStatus;
import edu.lpnu.saas.billing.model.enums.SubscriptionPlan;
import edu.lpnu.saas.billing.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final StripeClient stripeClient;
    private final PaymentRepository paymentRepository;
    private final SubscriptionService subscriptionService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Transactional
    public String createCheckoutSession(Long organizationId, String customerEmail, SubscriptionPlan plan) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(frontendUrl + "/organizations/" + organizationId + "/billing?success=true")
                    .setCancelUrl(frontendUrl + "/organizations/" + organizationId + "/billing?canceled=true")
                    .setCustomerEmail(customerEmail)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPrice(plan.getStripePriceId())
                                    .build()
                    )
                    .setSubscriptionData(
                            SessionCreateParams.SubscriptionData.builder()
                                    .putMetadata("organizationId", organizationId.toString())
                                    .putMetadata("plan", plan.name())
                                    .build()
                    )
                    .putMetadata("organizationId", organizationId.toString())
                    .putMetadata("plan", plan.name())
                    .build();

            Session session = stripeClient.v1().checkout().sessions().create(params);

            Payment payment = Payment.builder()
                    .organizationId(organizationId)
                    .status(PaymentStatus.PENDING)
                    .stripeSessionId(session.getId())
                    .build();
            paymentRepository.save(payment);

            return session.getUrl();

        } catch (StripeException e) {
            throw new PaymentException("Не вдалося створити платіжну сесію");
        }
    }

    public void cancelStripeSubscription(String stripeSubscriptionId) throws StripeException {
        stripeClient.v1().subscriptions().retrieve(stripeSubscriptionId);

        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setCancelAtPeriodEnd(true)
                .build();

        stripeClient.v1().subscriptions().update(stripeSubscriptionId, params);
    }

    @Transactional
    public void handleCheckoutSessionCompleted(Session session) {
        Payment payment = paymentRepository.findByStripeSessionId(session.getId())
                .orElseThrow(() -> new NotFoundException("Платіж не знайдено"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        Long orgId = Long.parseLong(session.getMetadata().get("organizationId"));
        SubscriptionPlan plan = SubscriptionPlan.valueOf(session.getMetadata().get("plan"));
        String stripeSubscriptionId = session.getSubscription();

        subscriptionService.activatePlan(orgId, plan, stripeSubscriptionId);
    }

    public void handleInvoicePaid(Invoice invoice) {
        if ("subscription_create".equals(invoice.getBillingReason())) {
            return;
        }

        String stripeSubscriptionId = null;

        if (invoice.getParent() != null
                && "subscription_details".equals(invoice.getParent().getType())
                && invoice.getParent().getSubscriptionDetails() != null) {

            stripeSubscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();
        }

        if (stripeSubscriptionId != null) {
            subscriptionService.renewSubscriptionByStripeId(stripeSubscriptionId);
        }
    }

    @Transactional
    public void handleCheckoutSessionExpired(Session session) {
        paymentRepository.findByStripeSessionId(session.getId()).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        });
    }

    public void handleInvoicePaymentFailed(Invoice invoice) {
        String stripeSubscriptionId = null;

        if (invoice.getParent() != null
                && "subscription_details".equals(invoice.getParent().getType())
                && invoice.getParent().getSubscriptionDetails() != null) {

            stripeSubscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();
        }

        if (stripeSubscriptionId != null) {
            subscriptionService.handleFailedRenewal(stripeSubscriptionId);
        }
    }

    @Transactional
    public void deletePaymentsByOrganizationId(Long organizationId) {
        paymentRepository.deleteByOrganizationId(organizationId);
    }
}