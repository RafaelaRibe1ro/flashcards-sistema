package com.rafaelaribeiro.categoria_service.service;

import com.rafaelaribeiro.categoria_service.dto.CategoriaRequestDTO;
import com.rafaelaribeiro.categoria_service.dto.CategoriaResponseDTO;
import com.rafaelaribeiro.categoria_service.messaging.CategoriaEventPublisher;
import com.rafaelaribeiro.categoria_service.model.Categoria;
import com.rafaelaribeiro.categoria_service.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaEventPublisher eventPublisher;

    public CategoriaServiceImpl(CategoriaRepository repository, CategoriaEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria(dto.nome(), dto.descricao());
        Categoria salva = repository.save(categoria);
        eventPublisher.publicar(salva, "criada");
        return toDTO(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaResponseDTO> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<CategoriaResponseDTO> atualizar(Long id, CategoriaRequestDTO dto) {
        return repository.findById(id).map(categoria -> {
            categoria.setNome(dto.nome());
            categoria.setDescricao(dto.descricao());
            Categoria atualizada = repository.save(categoria);
            eventPublisher.publicar(atualizada, "atualizada");
            return toDTO(atualizada);
        });
    }

    @Override
    public boolean deletar(Long id) {
        return repository.findById(id).map(categoria -> {
            repository.deleteById(id);
            eventPublisher.publicar(categoria, "deletada");
            return true;
        }).orElse(false);
    }

    private CategoriaResponseDTO toDTO(Categoria c) {
        return new CategoriaResponseDTO(c.getId(), c.getNome(), c.getDescricao(), c.getCriadoEm());
    }
}
