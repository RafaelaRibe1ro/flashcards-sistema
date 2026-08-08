package com.rafaelaribeiro.categoria_service.config;

import com.rafaelaribeiro.categoria_service.model.Categoria;
import com.rafaelaribeiro.categoria_service.repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository repository;

    public DataInitializer(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.saveAll(List.of(
                new Categoria("Java", "Conceitos da linguagem Java e orientação a objetos"),
                new Categoria("Spring Boot", "Framework para desenvolvimento de aplicações Java"),
                new Categoria("React", "Biblioteca JavaScript para construção de interfaces"),
                new Categoria("Arquitetura de Software", "Padrões e práticas de arquitetura, incluindo microsserviços")
            ));
        }
    }
}
