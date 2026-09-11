package com.rafaelaribeiro.sistema_flashcards.messaging;

public record FlashcardHistoricoEvent(Long flashcardId, String acao, String pergunta, String resposta) {
}
