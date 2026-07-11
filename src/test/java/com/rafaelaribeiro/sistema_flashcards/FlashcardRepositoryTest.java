package com.rafaelaribeiro.sistema_flashcards;

import com.rafaelaribeiro.sistema_flashcards.model.Flashcard;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FlashcardRepositoryTest {

    @Autowired
    private FlashcardRepository repository;

    @Test
    void deveSalvarERecuperarFlashcard() {
        Flashcard flashcard = new Flashcard("O que é JPA?", "Java Persistence API.");
        Flashcard salvo = repository.save(flashcard);

        Optional<Flashcard> encontrado = repository.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getPergunta()).isEqualTo("O que é JPA?");
        assertThat(encontrado.get().getCriadoEm()).isNotNull();
    }

    @Test
    void deveBuscarPorPerguntaIgnorandoCase() {
        repository.save(new Flashcard("O que é Spring Boot?", "Framework Java."));
        repository.save(new Flashcard("O que é JPA?", "API de persistência."));

        List<Flashcard> resultado = repository.findByPerguntaContainingIgnoreCase("spring");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPergunta()).containsIgnoringCase("spring");
    }

    @Test
    void deveListarEmOrdemDecrescenteDeCriacao() {
        repository.save(new Flashcard("Primeiro", "Resposta 1"));
        repository.save(new Flashcard("Segundo", "Resposta 2"));
        repository.save(new Flashcard("Terceiro", "Resposta 3"));

        List<Flashcard> resultado = repository.findAllByOrderByCriadoEmDesc();

        assertThat(resultado).hasSize(3);
        assertThat(resultado.get(0).getCriadoEm())
                .isAfterOrEqualTo(resultado.get(1).getCriadoEm());
        assertThat(resultado.get(1).getCriadoEm())
                .isAfterOrEqualTo(resultado.get(2).getCriadoEm());
    }

    @Test
    void deveDeletarFlashcard() {
        Flashcard flashcard = repository.save(new Flashcard("Para deletar", "Resposta."));
        Long id = flashcard.getId();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void deveRetornarVazioParaIdInexistente() {
        Optional<Flashcard> resultado = repository.findById(999L);
        assertThat(resultado).isEmpty();
    }
}
