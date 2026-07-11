package com.rafaelaribeiro.sistema_flashcards;

import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardHistoricoResponseDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardRequestDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardResponseDTO;
import com.rafaelaribeiro.sistema_flashcards.model.Flashcard;
import com.rafaelaribeiro.sistema_flashcards.model.FlashcardHistorico;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardHistoricoRepository;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardRepository;
import com.rafaelaribeiro.sistema_flashcards.service.FlashcardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashcardServiceTest {

    @Mock
    private FlashcardRepository repository;

    @Mock
    private FlashcardHistoricoRepository historicoRepository;

    @InjectMocks
    private FlashcardServiceImpl service;

    @Test
    void deveCriarFlashcardERegistrarHistorico() {
        FlashcardRequestDTO dto = new FlashcardRequestDTO("O que é JPA?", "API de persistência.");

        Flashcard flashcardSalvo = new Flashcard("O que é JPA?", "API de persistência.");
        flashcardSalvo.setId(1L);
        flashcardSalvo.setCriadoEm(LocalDateTime.now());

        when(repository.save(any(Flashcard.class))).thenReturn(flashcardSalvo);

        FlashcardResponseDTO resultado = service.criar(dto);

        assertThat(resultado.pergunta()).isEqualTo("O que é JPA?");
        verify(historicoRepository).save(any(FlashcardHistorico.class));

        ArgumentCaptor<FlashcardHistorico> captor = ArgumentCaptor.forClass(FlashcardHistorico.class);
        verify(historicoRepository).save(captor.capture());
        assertThat(captor.getValue().getAcao()).isEqualTo("CRIADO");
    }

    @Test
    void deveListarTodosFlashcards() {
        Flashcard f1 = flashcardComId(1L, "Pergunta 1", "Resposta 1");
        Flashcard f2 = flashcardComId(2L, "Pergunta 2", "Resposta 2");
        when(repository.findAll()).thenReturn(List.of(f1, f2));

        List<FlashcardResponseDTO> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).pergunta()).isEqualTo("Pergunta 1");
    }

    @Test
    void deveBuscarPorIdExistente() {
        Flashcard flashcard = flashcardComId(1L, "O que é Spring?", "Framework.");
        when(repository.findById(1L)).thenReturn(Optional.of(flashcard));

        Optional<FlashcardResponseDTO> resultado = service.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().pergunta()).isEqualTo("O que é Spring?");
    }

    @Test
    void deveBuscarPorIdInexistenteRetornarVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<FlashcardResponseDTO> resultado = service.buscarPorId(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveAtualizarFlashcardERegistrarHistorico() {
        Flashcard flashcard = flashcardComId(1L, "Pergunta antiga", "Resposta antiga");
        when(repository.findById(1L)).thenReturn(Optional.of(flashcard));
        when(repository.save(any(Flashcard.class))).thenReturn(flashcard);

        FlashcardRequestDTO dto = new FlashcardRequestDTO("Pergunta nova", "Resposta nova");
        Optional<FlashcardResponseDTO> resultado = service.atualizar(1L, dto);

        assertThat(resultado).isPresent();
        ArgumentCaptor<FlashcardHistorico> captor = ArgumentCaptor.forClass(FlashcardHistorico.class);
        verify(historicoRepository).save(captor.capture());
        assertThat(captor.getValue().getAcao()).isEqualTo("ATUALIZADO");
    }

    @Test
    void deveAtualizarInexistenteRetornarVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<FlashcardResponseDTO> resultado = service.atualizar(99L, new FlashcardRequestDTO("x", "y"));

        assertThat(resultado).isEmpty();
        verify(historicoRepository, never()).save(any());
    }

    @Test
    void deveDeletarFlashcardERegistrarHistorico() {
        Flashcard flashcard = flashcardComId(1L, "Pergunta", "Resposta");
        when(repository.findById(1L)).thenReturn(Optional.of(flashcard));

        boolean resultado = service.deletar(1L);

        assertThat(resultado).isTrue();
        ArgumentCaptor<FlashcardHistorico> captor = ArgumentCaptor.forClass(FlashcardHistorico.class);
        verify(historicoRepository).save(captor.capture());
        assertThat(captor.getValue().getAcao()).isEqualTo("DELETADO");
        verify(repository).deleteById(1L);
    }

    @Test
    void deveDeletarInexistenteRetornarFalso() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        boolean resultado = service.deletar(99L);

        assertThat(resultado).isFalse();
        verify(historicoRepository, never()).save(any());
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deveBuscarHistoricoCompleto() {
        FlashcardHistorico h1 = new FlashcardHistorico(1L, "CRIADO", "P1", "R1");
        FlashcardHistorico h2 = new FlashcardHistorico(2L, "CRIADO", "P2", "R2");
        when(historicoRepository.findAllByOrderByAlteradoEmDesc()).thenReturn(List.of(h1, h2));

        List<FlashcardHistoricoResponseDTO> resultado = service.buscarHistorico();

        assertThat(resultado).hasSize(2);
    }

    @Test
    void deveBuscarHistoricoPorFlashcard() {
        FlashcardHistorico h1 = new FlashcardHistorico(1L, "CRIADO", "P1", "R1");
        FlashcardHistorico h2 = new FlashcardHistorico(1L, "ATUALIZADO", "P1 v2", "R1 v2");
        when(historicoRepository.findByFlashcardIdOrderByAlteradoEmDesc(1L)).thenReturn(List.of(h2, h1));

        List<FlashcardHistoricoResponseDTO> resultado = service.buscarHistoricoPorFlashcard(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).acao()).isEqualTo("ATUALIZADO");
    }

    private Flashcard flashcardComId(Long id, String pergunta, String resposta) {
        Flashcard f = new Flashcard(pergunta, resposta);
        f.setId(id);
        f.setCriadoEm(LocalDateTime.now());
        return f;
    }
}
