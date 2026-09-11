package com.rafaelaribeiro.categoria_service.messaging;

import java.time.LocalDateTime;

public record CategoriaEvent(Long id, String nome, String descricao, String tipoEvento, LocalDateTime ocorridoEm) {
}
