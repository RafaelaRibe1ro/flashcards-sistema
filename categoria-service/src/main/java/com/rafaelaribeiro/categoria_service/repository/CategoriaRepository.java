package com.rafaelaribeiro.categoria_service.repository;

import com.rafaelaribeiro.categoria_service.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
