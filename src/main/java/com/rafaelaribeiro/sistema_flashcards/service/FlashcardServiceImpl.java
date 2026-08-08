package com.rafaelaribeiro.sistema_flashcards.service;

import com.rafaelaribeiro.sistema_flashcards.client.CategoriaClient;
import com.rafaelaribeiro.sistema_flashcards.dto.CategoriaDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardHistoricoResponseDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardRequestDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardResponseDTO;
import com.rafaelaribeiro.sistema_flashcards.model.Flashcard;
import com.rafaelaribeiro.sistema_flashcards.model.FlashcardHistorico;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardHistoricoRepository;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FlashcardServiceImpl implements FlashcardService {

    private static final Logger log = LoggerFactory.getLogger(FlashcardServiceImpl.class);

    private final FlashcardRepository repository;
    private final FlashcardHistoricoRepository historicoRepository;
    private final CategoriaClient categoriaClient;

    public FlashcardServiceImpl(FlashcardRepository repository,
                                FlashcardHistoricoRepository historicoRepository,
                                CategoriaClient categoriaClient) {
        this.repository = repository;
        this.historicoRepository = historicoRepository;
        this.categoriaClient = categoriaClient;
    }

    @Override
    public FlashcardResponseDTO criar(FlashcardRequestDTO dto) {
        Flashcard flashcard = new Flashcard(dto.pergunta(), dto.resposta());
        flashcard.setCategoriaId(dto.categoriaId());
        Flashcard salvo = repository.save(flashcard);
        historicoRepository.save(new FlashcardHistorico(salvo.getId(), "CRIADO", salvo.getPergunta(), salvo.getResposta()));
        return toDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashcardResponseDTO> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<FlashcardResponseDTO> atualizar(Long id, FlashcardRequestDTO dto) {
        return repository.findById(id).map(flashcard -> {
            flashcard.setPergunta(dto.pergunta());
            flashcard.setResposta(dto.resposta());
            flashcard.setCategoriaId(dto.categoriaId());
            Flashcard atualizado = repository.save(flashcard);
            historicoRepository.save(new FlashcardHistorico(id, "ATUALIZADO", atualizado.getPergunta(), atualizado.getResposta()));
            return toDTO(atualizado);
        });
    }

    @Override
    public boolean deletar(Long id) {
        return repository.findById(id).map(flashcard -> {
            historicoRepository.save(new FlashcardHistorico(id, "DELETADO", flashcard.getPergunta(), flashcard.getResposta()));
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> buscarPorPergunta(String termo) {
        return repository.findByPerguntaContainingIgnoreCase(termo).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardHistoricoResponseDTO> buscarHistorico() {
        return historicoRepository.findAllByOrderByAlteradoEmDesc().stream()
                .map(this::toHistoricoDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardHistoricoResponseDTO> buscarHistoricoPorFlashcard(Long flashcardId) {
        return historicoRepository.findByFlashcardIdOrderByAlteradoEmDesc(flashcardId).stream()
                .map(this::toHistoricoDTO)
                .toList();
    }

    private FlashcardResponseDTO toDTO(Flashcard f) {
        return new FlashcardResponseDTO(f.getId(), f.getPergunta(), f.getResposta(), f.getCriadoEm(),
                f.getAtualizadoEm(), f.getCategoriaId(), buscarNomeCategoria(f.getCategoriaId()));
    }

    private String buscarNomeCategoria(Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        try {
            CategoriaDTO categoria = categoriaClient.buscarPorId(categoriaId);
            return categoria != null ? categoria.nome() : null;
        } catch (Exception e) {
            log.warn("Não foi possível obter a categoria {} do categoria-service: {}", categoriaId, e.getMessage());
            return null;
        }
    }

    private FlashcardHistoricoResponseDTO toHistoricoDTO(FlashcardHistorico h) {
        return new FlashcardHistoricoResponseDTO(h.getId(), h.getFlashcardId(), h.getAcao(), h.getPergunta(), h.getResposta(), h.getAlteradoEm());
    }
}
