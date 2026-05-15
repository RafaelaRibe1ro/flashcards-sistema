package com.rafaelaribeiro.sistema_flashcards.config;

import com.rafaelaribeiro.sistema_flashcards.model.Flashcard;
import com.rafaelaribeiro.sistema_flashcards.repository.FlashcardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final FlashcardRepository repository;

    public DataInitializer(FlashcardRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.saveAll(List.of(
                new Flashcard(
                    "O que é POO?",
                    "Programação Orientada a Objetos: paradigma que organiza o software em objetos que combinam estado (atributos) e comportamento (métodos)."
                ),
                new Flashcard(
                    "O que é o Spring Boot?",
                    "Framework Java que simplifica o desenvolvimento de aplicações Spring, eliminando configurações manuais com autoconfiguração e servidor embarcado."
                ),
                new Flashcard(
                    "Quais são os pilares da POO?",
                    "Encapsulamento, Herança, Polimorfismo e Abstração."
                ),
                new Flashcard(
                    "O que é uma API REST?",
                    "Interface que utiliza os métodos HTTP (GET, POST, PUT, DELETE) e princípios REST para comunicação stateless entre sistemas."
                )
            ));
        }
    }
}
