package com.rafaelaribeiro.sistema_flashcards.dto;

import java.time.LocalDateTime;

public record FlashcardResponseDTO(
    Long id,
    String pergunta,
    String resposta,
    LocalDateTime criadoEm
) {}
