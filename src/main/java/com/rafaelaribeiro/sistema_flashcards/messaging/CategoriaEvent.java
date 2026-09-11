package com.rafaelaribeiro.sistema_flashcards.messaging;

import java.time.LocalDateTime;

public record CategoriaEvent(Long id, String nome, String descricao, String tipoEvento, LocalDateTime ocorridoEm) {
}
