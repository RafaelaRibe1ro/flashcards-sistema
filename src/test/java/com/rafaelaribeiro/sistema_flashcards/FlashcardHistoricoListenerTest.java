package com.rafaelaribeiro.sistema_flashcards;

import com.rafaelaribeiro.sistema_flashcards.messaging.FlashcardHistoricoEvent;
import com.rafaelaribeiro.sistema_flashcards.messaging.FlashcardHistoricoListener;
import com.rafaelaribeiro.sistema_flashcards.model.FlashcardHistorico;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardHistoricoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FlashcardHistoricoListenerTest {

    @Mock
    private FlashcardHistoricoRepository repository;

    @InjectMocks
    private FlashcardHistoricoListener listener;

    @Test
    void deveGravarHistoricoAPartirDoEvento() {
        FlashcardHistoricoEvent evento = new FlashcardHistoricoEvent(1L, "CRIADO", "Pergunta", "Resposta");

        listener.handle(evento);

        ArgumentCaptor<FlashcardHistorico> captor = ArgumentCaptor.forClass(FlashcardHistorico.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getFlashcardId()).isEqualTo(1L);
        assertThat(captor.getValue().getAcao()).isEqualTo("CRIADO");
    }
}
