package com.rafaelaribeiro.categoria_service;

import com.rafaelaribeiro.categoria_service.model.Categoria;
import com.rafaelaribeiro.categoria_service.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository repository;

    @Test
    void deveSalvarERecuperarCategoria() {
        Categoria categoria = new Categoria("Java", "Linguagem de programação");
        Categoria salva = repository.save(categoria);

        Optional<Categoria> encontrada = repository.findById(salva.getId());

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNome()).isEqualTo("Java");
        assertThat(encontrada.get().getCriadoEm()).isNotNull();
    }

    @Test
    void deveDeletarCategoria() {
        Categoria categoria = repository.save(new Categoria("Para deletar", "Descrição"));
        Long id = categoria.getId();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void deveRetornarVazioParaIdInexistente() {
        Optional<Categoria> resultado = repository.findById(999L);
        assertThat(resultado).isEmpty();
    }
}
