package edu.lpnu.saas.notification.config;

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
    public TopicExchange eventExchange() {
        return new TopicExchange("notification.exchange");
    }

    @Bean
    public DirectExchange notificationDlx() {
        return new DirectExchange("notification.dlx");
    }

    @Bean
    public Queue notificationDlq() {
        return new Queue("notification.email.dlq");
    }

    @Bean
    public Binding notificationDlqBinding() {
        return BindingBuilder.bind(notificationDlq()).to(notificationDlx()).with("notification.dead");
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable("notification.email.queue")
                .withArgument("x-dead-letter-exchange", "notification.dlx")
                .withArgument("x-dead-letter-routing-key", "notification.dead")
                .build();
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(eventExchange()).with("notification.email.#");
    }
}