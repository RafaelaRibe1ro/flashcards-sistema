package com.rafaelaribeiro.sistema_flashcards.service;

import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardRequestDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardResponseDTO;
import com.rafaelaribeiro.sistema_flashcards.model.Flashcard;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository repository;

    public FlashcardServiceImpl(FlashcardRepository repository) {
        this.repository = repository;
    }

    @Override
    public FlashcardResponseDTO criar(FlashcardRequestDTO dto) {
        Flashcard flashcard = new Flashcard(dto.pergunta(), dto.resposta());
        return toDTO(repository.save(flashcard));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public boolean deletar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private FlashcardResponseDTO toDTO(Flashcard f) {
        return new FlashcardResponseDTO(f.getId(), f.getPergunta(), f.getResposta(), f.getCriadoEm());
    }
}
