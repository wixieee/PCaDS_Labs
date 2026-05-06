package edu.lpnu.saas.billing.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange organizationExchange() {
        return new TopicExchange("organization.exchange");
    }

    @Bean
    public TopicExchange billingExchange() {
        return new TopicExchange("billing.exchange");
    }

    @Bean
    public DirectExchange billingDlx() {
        return new DirectExchange("billing.dlx");
    }

    @Bean
    public Queue billingDlq() {
        return new Queue("billing.dlq");
    }

    @Bean
    public Binding billingDlqBinding() {
        return BindingBuilder.bind(billingDlq()).to(billingDlx()).with("billing.dead");
    }

    @Bean
    public Queue billingOrgCreatedQueue() {
        return QueueBuilder.durable("billing.organization.created.queue")
                .withArgument("x-dead-letter-exchange", "billing.dlx")
                .withArgument("x-dead-letter-routing-key", "billing.dead")
                .build();
    }

    @Bean
    public Binding bindingOrgCreated() {
        return BindingBuilder.bind(billingOrgCreatedQueue()).to(organizationExchange()).with("organization.created");
    }

    @Bean
    public Queue billingOrgDeletedQueue() {
        return QueueBuilder.durable("billing.organization.deleted.queue")
                .withArgument("x-dead-letter-exchange", "billing.dlx")
                .withArgument("x-dead-letter-routing-key", "billing.dead")
                .build();
    }

    @Bean
    public Binding bindingOrgDeleted() {
        return BindingBuilder.bind(billingOrgDeletedQueue()).to(organizationExchange()).with("organization.deleted");
    }
}