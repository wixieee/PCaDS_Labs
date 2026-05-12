package edu.lpnu.saas.analysis.config;

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
    public DirectExchange analysisDlx() {
        return new DirectExchange("analysis.dlx");
    }

    @Bean
    public Queue analysisDlq() {
        return new Queue("analysis.dlq");
    }

    @Bean
    public Binding analysisDlqBinding() {
        return BindingBuilder.bind(analysisDlq()).to(analysisDlx()).with("analysis.dead");
    }

    @Bean
    public Queue analysisOrgCreatedQueue() {
        return QueueBuilder.durable("analysis.organization.created.queue")
                .withArgument("x-dead-letter-exchange", "analysis.dlx")
                .withArgument("x-dead-letter-routing-key", "analysis.dead")
                .build();
    }

    @Bean
    public Binding bindingOrgCreated() {
        return BindingBuilder.bind(analysisOrgCreatedQueue()).to(organizationExchange()).with("organization.created");
    }

    @Bean
    public Queue analysisOrgDeletedQueue() {
        return QueueBuilder.durable("analysis.organization.deleted.queue")
                .withArgument("x-dead-letter-exchange", "analysis.dlx")
                .withArgument("x-dead-letter-routing-key", "analysis.dead")
                .build();
    }

    @Bean
    public Binding bindingOrgDeleted() {
        return BindingBuilder.bind(analysisOrgDeletedQueue()).to(organizationExchange()).with("organization.deleted");
    }

    @Bean
    public Queue analysisSubPlanChangedQueue() {
        return QueueBuilder.durable("analysis.subscription.plan.changed.queue")
                .withArgument("x-dead-letter-exchange", "analysis.dlx")
                .withArgument("x-dead-letter-routing-key", "analysis.dead")
                .build();
    }

    @Bean
    public Binding bindingSubPlanChanged() {
        return BindingBuilder.bind(analysisSubPlanChangedQueue()).to(billingExchange()).with("subscription.plan.changed");
    }

    @Bean
    public Queue analysisSubRenewedQueue() {
        return QueueBuilder.durable("analysis.subscription.renewed.queue")
                .withArgument("x-dead-letter-exchange", "analysis.dlx")
                .withArgument("x-dead-letter-routing-key", "analysis.dead")
                .build();
    }

    @Bean
    public Binding bindingSubRenewed() {
        return BindingBuilder.bind(analysisSubRenewedQueue()).to(billingExchange()).with("subscription.renewed");
    }
}