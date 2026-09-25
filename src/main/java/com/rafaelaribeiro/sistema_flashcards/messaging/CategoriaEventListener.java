package com.rafaelaribeiro.sistema_flashcards.messaging;

import com.rafaelaribeiro.sistema_flashcards.config.RabbitMQConfig;
import com.rafaelaribeiro.sistema_flashcards.model.CategoriaCache;
import com.rafaelaribeiro.sistema_flashcards.repository.CategoriaCacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CategoriaEventListener {

    private static final Logger log = LoggerFactory.getLogger(CategoriaEventListener.class);

    private final CategoriaCacheRepository repository;

    public CategoriaEventListener(CategoriaCacheRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMQConfig.CATEGORIA_SYNC_QUEUE)
    public void handle(CategoriaEvent evento) {
        log.info("Evento de categoria recebido: {} (id {})", evento.tipoEvento(), evento.id());
        switch (evento.tipoEvento()) {
            case "criada", "atualizada" -> repository.save(new CategoriaCache(evento.id(), evento.nome(), evento.descricao()));
            case "deletada" -> repository.deleteById(evento.id());
            default -> log.warn("Tipo de evento de categoria desconhecido: {}", evento.tipoEvento());
        }
    }
}
