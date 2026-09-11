package com.rafaelaribeiro.sistema_flashcards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Réplica local (read-model) das categorias do categoria-service, mantida via
 * eventos do RabbitMQ. O id é o mesmo atribuído pelo categoria-service — não é
 * gerado aqui, pois este serviço não é o dono desse dado.
 */
@Entity
@Table(name = "categoria_cache")
public class CategoriaCache {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    public CategoriaCache() {}

    public CategoriaCache(Long id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.atualizadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
