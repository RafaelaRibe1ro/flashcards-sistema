package com.rafaelaribeiro.categoria_service.messaging;

import com.rafaelaribeiro.categoria_service.config.RabbitMQConfig;
import com.rafaelaribeiro.categoria_service.model.Categoria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CategoriaEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public CategoriaEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(Categoria categoria, String tipoEvento) {
        CategoriaEvent evento = new CategoriaEvent(categoria.getId(), categoria.getNome(),
                categoria.getDescricao(), tipoEvento, LocalDateTime.now());
        String routingKey = "categoria." + tipoEvento;
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.CATEGORIA_EVENTS_EXCHANGE, routingKey, evento);
            log.info("Evento '{}' publicado para a categoria {}", routingKey, categoria.getId());
        } catch (Exception e) {
            log.warn("Não foi possível publicar o evento '{}' da categoria {}: {}", routingKey, categoria.getId(), e.getMessage());
        }
    }
}
