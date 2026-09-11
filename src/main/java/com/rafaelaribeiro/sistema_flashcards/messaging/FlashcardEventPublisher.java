package com.rafaelaribeiro.sistema_flashcards.messaging;

import com.rafaelaribeiro.sistema_flashcards.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class FlashcardEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(FlashcardEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public FlashcardEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(FlashcardHistoricoEvent evento) {
        String routingKey = "flashcard." + evento.acao().toLowerCase();
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.FLASHCARD_EVENTS_EXCHANGE, routingKey, evento);
        } catch (Exception e) {
            log.warn("Não foi possível publicar o evento '{}' do flashcard {}: {}", routingKey, evento.flashcardId(), e.getMessage());
        }
    }
}
