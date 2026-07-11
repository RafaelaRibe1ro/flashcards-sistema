package com.rafaelaribeiro.sistema_flashcards.dto;

import java.time.LocalDateTime;

public record FlashcardHistoricoResponseDTO(
    Long id,
    Long flashcardId,
    String acao,
    String pergunta,
    String resposta,
    LocalDateTime alteradoEm
) {}
