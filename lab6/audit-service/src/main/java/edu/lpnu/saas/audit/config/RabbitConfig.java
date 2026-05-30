package edu.lpnu.saas.audit.config;

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
    public TopicExchange auditExchange() {
        return new TopicExchange("audit.exchange");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("audit.dlx");
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("audit.dlq");
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("audit.routing.dead");
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable("audit.queue")
                .withArgument("x-dead-letter-exchange", "audit.dlx")
                .withArgument("x-dead-letter-routing-key", "audit.routing.dead")
                .build();
    }

    @Bean
    public Binding auditBinding() {
        return BindingBuilder.bind(auditQueue()).to(auditExchange()).with("audit.#");
    }
}