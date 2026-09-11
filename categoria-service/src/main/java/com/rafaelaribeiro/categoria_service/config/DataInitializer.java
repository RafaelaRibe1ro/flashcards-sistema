package com.rafaelaribeiro.categoria_service.config;

import com.rafaelaribeiro.categoria_service.dto.CategoriaRequestDTO;
import com.rafaelaribeiro.categoria_service.repository.CategoriaRepository;
import com.rafaelaribeiro.categoria_service.service.CategoriaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository repository;
    private final CategoriaService service;

    public DataInitializer(CategoriaRepository repository, CategoriaService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            List.of(
                new CategoriaRequestDTO("Java", "Conceitos da linguagem Java e orientação a objetos"),
                new CategoriaRequestDTO("Spring Boot", "Framework para desenvolvimento de aplicações Java"),
                new CategoriaRequestDTO("React", "Biblioteca JavaScript para construção de interfaces"),
                new CategoriaRequestDTO("Arquitetura de Software", "Padrões e práticas de arquitetura, incluindo microsserviços")
            ).forEach(service::criar);
        }
    }
}
