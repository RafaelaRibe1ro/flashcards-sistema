package com.rafaelaribeiro.sistema_flashcards.dto;

import java.time.LocalDateTime;

public record CategoriaDTO(
    Long id,
    String nome,
    String descricao,
    LocalDateTime criadoEm
) {}
