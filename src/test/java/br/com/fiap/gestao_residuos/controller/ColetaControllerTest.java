package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.config.JacksonConfig;
import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Coleta;
import br.com.fiap.gestao_residuos.service.ColetaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ColetaController.class)
@Import(JacksonConfig.class)
@DisplayName("ColetaController - Testes Unitários")
public class ColetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ColetaService coletaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/coletas deve retornar lista de coletas com status 200")
    void listarColetas_deveRetornar200ComLista() throws Exception {
        Coleta c1 = new Coleta(1L, null, null, new Date(), "AGENDADO");
        Coleta c2 = new Coleta(2L, null, null, new Date(), "CONCLUIDO");
        when(coletaService.listarTodos()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/coletas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("AGENDADO")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is("CONCLUIDO")));

        verify(coletaService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/coletas deve retornar lista vazia com status 200")
    void listarColetas_semRegistros_deveRetornar200ComListaVazia() throws Exception {
        when(coletaService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/coletas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/coleta/{id} deve retornar coleta encontrada com status 200")
    void buscarColetaPorId_existente_deveRetornar200() throws Exception {
        Coleta coleta = new Coleta(1L, null, null, new Date(), "AGENDADO");
        when(coletaService.buscarPorId(1L)).thenReturn(Optional.of(coleta));

        mockMvc.perform(get("/api/coleta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("AGENDADO")));

        verify(coletaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/coleta/{id} deve retornar 404 quando coleta não existe")
    void buscarColetaPorId_inexistente_deveRetornar404() throws Exception {
        when(coletaService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/coleta/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/coleta deve criar coleta válida e retornar 201")
    void salvarColeta_dadosValidos_deveRetornar201() throws Exception {
        Coleta entrada = new Coleta(null, null, null, new Date(), "AGENDADO");
        Coleta salva   = new Coleta(1L, null, null, new Date(), "AGENDADO");
        when(coletaService.salvar(any(Coleta.class))).thenReturn(salva);

        mockMvc.perform(post("/api/coleta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("AGENDADO")));

        verify(coletaService).salvar(any(Coleta.class));
    }

    @Test
    @DisplayName("POST /api/coleta sem status deve retornar 400")
    void salvarColeta_semStatus_deveRetornar400() throws Exception {
        when(coletaService.salvar(any(Coleta.class)))
                .thenThrow(new InvalidDataException("Status é obrigatório"));

        mockMvc.perform(post("/api/coleta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataAgendada\": 1700000000000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")))
                .andExpect(jsonPath("$.message", containsString("Status é obrigatório")));
    }

    @Test
    @DisplayName("POST /api/coleta sem data agendada deve retornar 400")
    void salvarColeta_semDataAgendada_deveRetornar400() throws Exception {
        when(coletaService.salvar(any(Coleta.class)))
                .thenThrow(new InvalidDataException("Data agendada é obrigatória"));

        mockMvc.perform(post("/api/coleta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"AGENDADO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")));
    }

    @Test
    @DisplayName("PUT /api/coleta/{id} deve atualizar e retornar 200")
    void atualizarColeta_existente_deveRetornar200() throws Exception {
        Coleta atualizada = new Coleta(1L, null, null, new Date(), "CONCLUIDO");
        when(coletaService.atualizar(eq(1L), any(Coleta.class))).thenReturn(atualizada);

        mockMvc.perform(put("/api/coleta/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"CONCLUIDO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CONCLUIDO")));
    }

    @Test
    @DisplayName("PUT /api/coleta/{id} deve retornar 404 quando coleta não existe")
    void atualizarColeta_inexistente_deveRetornar404() throws Exception {
        when(coletaService.atualizar(eq(999L), any(Coleta.class)))
                .thenThrow(new ResourceNotFoundException("Coleta não encontrada com ID: 999"));

        mockMvc.perform(put("/api/coleta/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"CONCLUIDO\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }

    @Test
    @DisplayName("DELETE /api/coleta/{id} deve excluir e retornar 204")
    void excluirColeta_existente_deveRetornar204() throws Exception {
        doNothing().when(coletaService).excluir(1L);

        mockMvc.perform(delete("/api/coleta/1"))
                .andExpect(status().isNoContent());

        verify(coletaService).excluir(1L);
    }

    @Test
    @DisplayName("DELETE /api/coleta/{id} deve retornar 404 quando coleta não existe")
    void excluirColeta_inexistente_deveRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Coleta não encontrada com ID: 999"))
                .when(coletaService).excluir(999L);

        mockMvc.perform(delete("/api/coleta/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }
}
