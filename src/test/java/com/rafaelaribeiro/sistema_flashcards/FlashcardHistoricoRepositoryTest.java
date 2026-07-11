package com.rafaelaribeiro.sistema_flashcards;

import com.rafaelaribeiro.sistema_flashcards.model.FlashcardHistorico;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardHistoricoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FlashcardHistoricoRepositoryTest {

    @Autowired
    private FlashcardHistoricoRepository repository;

    @Test
    void deveSalvarRegistroDeHistorico() {
        FlashcardHistorico historico = new FlashcardHistorico(1L, "CRIADO", "O que é JPA?", "API de persistência.");
        FlashcardHistorico salvo = repository.save(historico);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getAcao()).isEqualTo("CRIADO");
        assertThat(salvo.getAlteradoEm()).isNotNull();
    }

    @Test
    void deveBuscarHistoricoPorFlashcardId() {
        repository.save(new FlashcardHistorico(10L, "CRIADO", "Pergunta A", "Resposta A"));
        repository.save(new FlashcardHistorico(10L, "ATUALIZADO", "Pergunta A v2", "Resposta A v2"));
        repository.save(new FlashcardHistorico(20L, "CRIADO", "Pergunta B", "Resposta B"));

        List<FlashcardHistorico> resultado = repository.findByFlashcardIdOrderByAlteradoEmDesc(10L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(h -> h.getFlashcardId().equals(10L));
    }

    @Test
    void deveListarTodoHistoricoEmOrdemDecrescente() {
        repository.save(new FlashcardHistorico(1L, "CRIADO", "Pergunta 1", "Resposta 1"));
        repository.save(new FlashcardHistorico(2L, "CRIADO", "Pergunta 2", "Resposta 2"));
        repository.save(new FlashcardHistorico(1L, "DELETADO", "Pergunta 1", "Resposta 1"));

        List<FlashcardHistorico> resultado = repository.findAllByOrderByAlteradoEmDesc();

        assertThat(resultado).hasSize(3);
        assertThat(resultado.get(0).getAlteradoEm())
                .isAfterOrEqualTo(resultado.get(1).getAlteradoEm());
        assertThat(resultado.get(1).getAlteradoEm())
                .isAfterOrEqualTo(resultado.get(2).getAlteradoEm());
    }

    @Test
    void deveRetornarListaVaziaParaFlashcardSemHistorico() {
        List<FlashcardHistorico> resultado = repository.findByFlashcardIdOrderByAlteradoEmDesc(999L);
        assertThat(resultado).isEmpty();
    }
}
