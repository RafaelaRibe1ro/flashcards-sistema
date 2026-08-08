package com.rafaelaribeiro.sistema_flashcards.client;

import com.rafaelaribeiro.sistema_flashcards.dto.CategoriaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "categoria-service", url = "${categoria.service.url}")
public interface CategoriaClient {

    @GetMapping("/api/categorias/{id}")
    CategoriaDTO buscarPorId(@PathVariable Long id);
}
