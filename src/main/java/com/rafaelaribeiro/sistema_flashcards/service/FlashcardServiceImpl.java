package com.rafaelaribeiro.sistema_flashcards.service;

import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardHistoricoResponseDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardRequestDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardResponseDTO;
import com.rafaelaribeiro.sistema_flashcards.model.Flashcard;
import com.rafaelaribeiro.sistema_flashcards.model.FlashcardHistorico;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardHistoricoRepository;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository repository;
    private final FlashcardHistoricoRepository historicoRepository;

    public FlashcardServiceImpl(FlashcardRepository repository,
                                FlashcardHistoricoRepository historicoRepository) {
        this.repository = repository;
        this.historicoRepository = historicoRepository;
    }

    @Override
    public FlashcardResponseDTO criar(FlashcardRequestDTO dto) {
        Flashcard flashcard = new Flashcard(dto.pergunta(), dto.resposta());
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
        return new FlashcardResponseDTO(f.getId(), f.getPergunta(), f.getResposta(), f.getCriadoEm(), f.getAtualizadoEm());
    }

    private FlashcardHistoricoResponseDTO toHistoricoDTO(FlashcardHistorico h) {
        return new FlashcardHistoricoResponseDTO(h.getId(), h.getFlashcardId(), h.getAcao(), h.getPergunta(), h.getResposta(), h.getAlteradoEm());
    }
}
