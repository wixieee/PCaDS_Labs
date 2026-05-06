package edu.lpnu.saas.organization.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange("organization.exchange");
    }
}