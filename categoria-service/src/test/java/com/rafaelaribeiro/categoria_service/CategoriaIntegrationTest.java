package com.rafaelaribeiro.categoria_service;

import com.jayway.jsonpath.JsonPath;
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
class CategoriaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarBuscarAtualizarEDeletarCategoria() throws Exception {
        String body = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Docker\",\"descricao\":\"Containers\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Docker"))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(body, "$.id");

        mockMvc.perform(get("/api/categorias/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Containers"));

        mockMvc.perform(put("/api/categorias/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Kubernetes\",\"descricao\":\"Orquestracao\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Kubernetes"));

        mockMvc.perform(delete("/api/categorias/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categorias/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarCategoriasCriadasNoInicio() throws Exception {
        // o DataInitializer cria categorias de exemplo ao subir a aplicação
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void deveRetornar404AoDeletarCategoriaInexistente() throws Exception {
        mockMvc.perform(delete("/api/categorias/12345"))
                .andExpect(status().isNotFound());
    }
}
