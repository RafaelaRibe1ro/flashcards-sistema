package com.rafaelaribeiro.sistema_flashcards.controller;

import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardRequestDTO;
import com.rafaelaribeiro.sistema_flashcards.dto.FlashcardResponseDTO;
import com.rafaelaribeiro.sistema_flashcards.service.FlashcardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    private final FlashcardService service;

    public FlashcardController(FlashcardService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FlashcardResponseDTO> criar(@RequestBody FlashcardRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<FlashcardResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean deletado = service.deletar(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
