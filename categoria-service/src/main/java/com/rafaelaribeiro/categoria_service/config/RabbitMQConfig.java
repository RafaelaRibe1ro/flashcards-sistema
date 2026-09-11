package com.rafaelaribeiro.categoria_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CATEGORIA_EVENTS_EXCHANGE = "categoria.events";

    @Bean
    public TopicExchange categoriaEventsExchange() {
        return new TopicExchange(CATEGORIA_EVENTS_EXCHANGE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
