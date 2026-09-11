package com.rafaelaribeiro.sistema_flashcards.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CATEGORIA_EVENTS_EXCHANGE = "categoria.events";
    public static final String CATEGORIA_SYNC_QUEUE = "monolito.categoria.sync.queue";

    public static final String FLASHCARD_EVENTS_EXCHANGE = "flashcard.events";
    public static final String FLASHCARD_HISTORICO_QUEUE = "flashcard.historico.queue";
    public static final String FLASHCARD_HISTORICO_DLQ = "flashcard.historico.queue.dlq";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    // Sincronização de categorias

    @Bean
    public TopicExchange categoriaEventsExchange() {
        return new TopicExchange(CATEGORIA_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue categoriaSyncQueue() {
        return QueueBuilder.durable(CATEGORIA_SYNC_QUEUE).build();
    }

    @Bean
    public Binding categoriaSyncBinding() {
        return BindingBuilder.bind(categoriaSyncQueue()).to(categoriaEventsExchange()).with("categoria.*");
    }

    // Histórico de flashcards

    @Bean
    public TopicExchange flashcardEventsExchange() {
        return new TopicExchange(FLASHCARD_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue flashcardHistoricoDlq() {
        return QueueBuilder.durable(FLASHCARD_HISTORICO_DLQ).build();
    }

    @Bean
    public Queue flashcardHistoricoQueue() {
        return QueueBuilder.durable(FLASHCARD_HISTORICO_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", FLASHCARD_HISTORICO_DLQ)
                .build();
    }

    @Bean
    public Binding flashcardHistoricoBinding() {
        return BindingBuilder.bind(flashcardHistoricoQueue()).to(flashcardEventsExchange()).with("flashcard.*");
    }
}
