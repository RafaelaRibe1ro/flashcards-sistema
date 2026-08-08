package com.rafaelaribeiro.categoria_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(
        @NotBlank String nome,
        String descricao
) {}
