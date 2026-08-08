package com.rafaelaribeiro.categoria_service.service;

import com.rafaelaribeiro.categoria_service.dto.CategoriaRequestDTO;
import com.rafaelaribeiro.categoria_service.dto.CategoriaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {

    CategoriaResponseDTO criar(CategoriaRequestDTO dto);

    List<CategoriaResponseDTO> listarTodos();

    Optional<CategoriaResponseDTO> buscarPorId(Long id);

    Optional<CategoriaResponseDTO> atualizar(Long id, CategoriaRequestDTO dto);

    boolean deletar(Long id);
}
