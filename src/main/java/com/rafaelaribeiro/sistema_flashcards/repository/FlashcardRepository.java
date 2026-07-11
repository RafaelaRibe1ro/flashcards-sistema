package com.rafaelaribeiro.sistema_flashcards.repository;

import com.rafaelaribeiro.sistema_flashcards.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    List<Flashcard> findByPerguntaContainingIgnoreCase(String termo);

    List<Flashcard> findAllByOrderByCriadoEmDesc();
}
