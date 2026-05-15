package com.rafaelaribeiro.sistema_flashcards.service;

import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardRequestDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardResponseDTO;

import java.util.List;

public interface FlashcardService {
    FlashcardResponseDTO criar(FlashcardRequestDTO dto);
    List<FlashcardResponseDTO> listarTodos();
    boolean deletar(Long id);
}
