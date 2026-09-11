package com.rafaelaribeiro.sistema_flashcards.messaging;

import com.rafaelaribeiro.sistema_flashcards.config.RabbitMQConfig;
import com.rafaelaribeiro.sistema_flashcards.model.FlashcardHistorico;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardHistoricoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FlashcardHistoricoListener {

    private final FlashcardHistoricoRepository repository;

    public FlashcardHistoricoListener(FlashcardHistoricoRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMQConfig.FLASHCARD_HISTORICO_QUEUE)
    public void handle(FlashcardHistoricoEvent evento) {
        repository.save(new FlashcardHistorico(evento.flashcardId(), evento.acao(), evento.pergunta(), evento.resposta()));
    }
}
