package com.rafaelaribeiro.sistema_flashcards;

import com.jayway.jsonpath.JsonPath;
import com.rafaelaribeiro.sistema_flashcards.model.CategoriaCache;
import com.rafaelaribeiro.sistema_flashcards.repository.CategoriaCacheRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Teste de integração: sobe a aplicação inteira e testa controller + service + repositório + banco juntos
@SpringBootTest
@AutoConfigureMockMvc
class FlashcardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaCacheRepository categoriaCacheRepository;

    @Test
    void deveCriarBuscarAtualizarEDeletarFlashcard() throws Exception {
        String body = mockMvc.perform(post("/api/flashcards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pergunta\":\"O que e Docker?\",\"resposta\":\"Plataforma de containers\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pergunta").value("O que e Docker?"))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(body, "$.id");

        mockMvc.perform(get("/api/flashcards/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resposta").value("Plataforma de containers"));

        mockMvc.perform(put("/api/flashcards/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pergunta\":\"O que e Kubernetes?\",\"resposta\":\"Orquestrador de containers\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pergunta").value("O que e Kubernetes?"));

        mockMvc.perform(delete("/api/flashcards/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/flashcards/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarNomeDaCategoriaQueEstaNoCache() throws Exception {
        categoriaCacheRepository.save(new CategoriaCache(99L, "DevOps", "Docker e Kubernetes"));

        mockMvc.perform(post("/api/flashcards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pergunta\":\"O que e CI/CD?\",\"resposta\":\"Integracao e entrega continua\",\"categoriaId\":99}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoriaNome").value("DevOps"));
    }

    @Test
    void deveRetornar404AoBuscarFlashcardInexistente() throws Exception {
        mockMvc.perform(get("/api/flashcards/12345"))
                .andExpect(status().isNotFound());
    }
}
