package com.rafaelaribeiro.sistema_flashcards;

import com.rafaelaribeiro.sistema_flashcards.messaging.CategoriaEvent;
import com.rafaelaribeiro.sistema_flashcards.messaging.CategoriaEventListener;
import com.rafaelaribeiro.sistema_flashcards.model.CategoriaCache;
import com.rafaelaribeiro.sistema_flashcards.repository.CategoriaCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoriaEventListenerTest {

    @Mock
    private CategoriaCacheRepository repository;

    @InjectMocks
    private CategoriaEventListener listener;

    @Test
    void deveGravarNoCacheQuandoCategoriaCriada() {
        CategoriaEvent evento = new CategoriaEvent(1L, "Java", "Linguagem", "criada", LocalDateTime.now());

        listener.handle(evento);

        ArgumentCaptor<CategoriaCache> captor = ArgumentCaptor.forClass(CategoriaCache.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(1L);
        assertThat(captor.getValue().getNome()).isEqualTo("Java");
    }

    @Test
    void deveAtualizarCacheQuandoCategoriaAtualizada() {
        CategoriaEvent evento = new CategoriaEvent(1L, "Java Atualizado", "Linguagem", "atualizada", LocalDateTime.now());

        listener.handle(evento);

        ArgumentCaptor<CategoriaCache> captor = ArgumentCaptor.forClass(CategoriaCache.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Java Atualizado");
    }

    @Test
    void deveRemoverDoCacheQuandoCategoriaDeletada() {
        CategoriaEvent evento = new CategoriaEvent(1L, "Java", "Linguagem", "deletada", LocalDateTime.now());

        listener.handle(evento);

        verify(repository).deleteById(1L);
    }
}
