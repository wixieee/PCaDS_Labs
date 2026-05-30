package edu.lpnu.saas.auth.config;

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
    public DirectExchange iamDlx() {
        return new DirectExchange("iam.dlx");
    }

    @Bean
    public Queue iamDlq() {
        return new Queue("iam.dlq");
    }

    @Bean
    public Binding iamDlqBinding() {
        return BindingBuilder.bind(iamDlq()).to(iamDlx()).with("iam.dead");
    }

    @Bean
    public Queue iamOrganizationDeletedQueue() {
        return QueueBuilder.durable("iam.organization.deleted.queue")
                .withArgument("x-dead-letter-exchange", "iam.dlx")
                .withArgument("x-dead-letter-routing-key", "iam.dead")
                .build();
    }

    @Bean
    public Binding bindingOrganizationDeleted() {
        return BindingBuilder
                .bind(iamOrganizationDeletedQueue())
                .to(organizationExchange())
                .with("organization.deleted");
    }
}