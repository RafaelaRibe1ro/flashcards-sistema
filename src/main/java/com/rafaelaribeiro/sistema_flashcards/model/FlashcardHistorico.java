package com.rafaelaribeiro.sistema_flashcards.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flashcard_historico")
public class FlashcardHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long flashcardId;

    @Column(nullable = false)
    private String acao;

    @Column(nullable = false)
    private String pergunta;

    @Column(nullable = false, length = 2000)
    private String resposta;

    @Column(nullable = false)
    private LocalDateTime alteradoEm;

    public FlashcardHistorico() {}

    public FlashcardHistorico(Long flashcardId, String acao, String pergunta, String resposta) {
        this.flashcardId = flashcardId;
        this.acao = acao;
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.alteradoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Long getFlashcardId() { return flashcardId; }

    public String getAcao() { return acao; }

    public String getPergunta() { return pergunta; }

    public String getResposta() { return resposta; }

    public LocalDateTime getAlteradoEm() { return alteradoEm; }
}
