package com.rafaelaribeiro.categoria_service;

import com.rafaelaribeiro.categoria_service.dto.CategoriaRequestDTO;
import com.rafaelaribeiro.categoria_service.dto.CategoriaResponseDTO;
import com.rafaelaribeiro.categoria_service.model.Categoria;
import com.rafaelaribeiro.categoria_service.repository.CategoriaRepository;
import com.rafaelaribeiro.categoria_service.service.CategoriaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaServiceImpl service;

    @Test
    void deveCriarCategoria() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Java", "Linguagem de programação");

        Categoria categoriaSalva = new Categoria("Java", "Linguagem de programação");
        categoriaSalva.setId(1L);
        categoriaSalva.setCriadoEm(LocalDateTime.now());

        when(repository.save(any(Categoria.class))).thenReturn(categoriaSalva);

        CategoriaResponseDTO resultado = service.criar(dto);

        assertThat(resultado.nome()).isEqualTo("Java");
    }

    @Test
    void deveListarTodasCategorias() {
        Categoria c1 = categoriaComId(1L, "Java");
        Categoria c2 = categoriaComId(2L, "Spring Boot");
        when(repository.findAll()).thenReturn(List.of(c1, c2));

        List<CategoriaResponseDTO> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nome()).isEqualTo("Java");
    }

    @Test
    void deveBuscarPorIdExistente() {
        Categoria categoria = categoriaComId(1L, "React");
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));

        Optional<CategoriaResponseDTO> resultado = service.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().nome()).isEqualTo("React");
    }

    @Test
    void deveBuscarPorIdInexistenteRetornarVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<CategoriaResponseDTO> resultado = service.buscarPorId(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveAtualizarCategoria() {
        Categoria categoria = categoriaComId(1L, "Nome antigo");
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        when(repository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaRequestDTO dto = new CategoriaRequestDTO("Nome novo", "Descrição nova");
        Optional<CategoriaResponseDTO> resultado = service.atualizar(1L, dto);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().nome()).isEqualTo("Nome novo");
    }

    @Test
    void deveAtualizarInexistenteRetornarVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<CategoriaResponseDTO> resultado = service.atualizar(99L, new CategoriaRequestDTO("x", "y"));

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveDeletarCategoriaExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = service.deletar(1L);

        assertThat(resultado).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    void deveDeletarInexistenteRetornarFalso() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean resultado = service.deletar(99L);

        assertThat(resultado).isFalse();
        verify(repository, never()).deleteById(any());
    }

    private Categoria categoriaComId(Long id, String nome) {
        Categoria c = new Categoria(nome, "Descrição de " + nome);
        c.setId(id);
        c.setCriadoEm(LocalDateTime.now());
        return c;
    }
}
