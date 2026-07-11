package com.rafaelaribeiro.sistema_flashcards.repository;

import com.rafaelaribeiro.sistema_flashcards.model.FlashcardHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardHistoricoRepository extends JpaRepository<FlashcardHistorico, Long> {

    List<FlashcardHistorico> findByFlashcardIdOrderByAlteradoEmDesc(Long flashcardId);

    List<FlashcardHistorico> findAllByOrderByAlteradoEmDesc();
}
