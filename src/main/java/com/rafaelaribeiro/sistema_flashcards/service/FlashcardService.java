package com.rafaelaribeiro.sistema_flashcards.service;

import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardHistoricoResponseDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardRequestDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardResponseDTO;

import java.util.List;
import java.util.Optional;

public interface FlashcardService {

    FlashcardResponseDTO criar(FlashcardRequestDTO dto);

    List<FlashcardResponseDTO> listarTodos();

    Optional<FlashcardResponseDTO> buscarPorId(Long id);

    Optional<FlashcardResponseDTO> atualizar(Long id, FlashcardRequestDTO dto);

    boolean deletar(Long id);

    List<FlashcardResponseDTO> buscarPorPergunta(String termo);

    List<FlashcardHistoricoResponseDTO> buscarHistorico();

    List<FlashcardHistoricoResponseDTO> buscarHistoricoPorFlashcard(Long flashcardId);
}
