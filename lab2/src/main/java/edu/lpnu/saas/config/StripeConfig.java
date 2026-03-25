package edu.lpnu.saas.config;

import com.stripe.StripeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Bean
    public StripeClient stripeClient() {
        return new StripeClient(stripeApiKey);
    }
}