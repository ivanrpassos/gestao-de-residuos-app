package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.config.JacksonConfig;
import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Destinacao;
import br.com.fiap.gestao_residuos.service.DestinacaoService;
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

@WebMvcTest(DestinacaoController.class)
@Import(JacksonConfig.class)
@DisplayName("DestinacaoController - Testes Unitários")
public class DestinacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DestinacaoService destinacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/destinacoes deve retornar lista com status 200")
    void listarDestinacoes_deveRetornar200ComLista() throws Exception {
        Destinacao d1 = new Destinacao(1L, "PLASTICO", "Recicladora Norte", new Date(), 150.0);
        Destinacao d2 = new Destinacao(2L, "METAL",    "Siderúrgica Sul",   new Date(), 300.0);
        when(destinacaoService.listarTodos()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/destinacoes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].tipoMaterial", is("PLASTICO")))
                .andExpect(jsonPath("$[0].localDestino", is("Recicladora Norte")))
                .andExpect(jsonPath("$[1].tipoMaterial", is("METAL")));

        verify(destinacaoService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/destino/{id} deve retornar destinação com status 200")
    void buscarDestinacaoPorId_existente_deveRetornar200() throws Exception {
        Destinacao dest = new Destinacao(1L, "VIDRO", "Vidraria Central", new Date(), 80.0);
        when(destinacaoService.buscarPorId(1L)).thenReturn(Optional.of(dest));

        mockMvc.perform(get("/api/destino/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.tipoMaterial", is("VIDRO")))
                .andExpect(jsonPath("$.localDestino", is("Vidraria Central")))
                .andExpect(jsonPath("$.quantidadeKg", is(80.0)));

        verify(destinacaoService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/destino/{id} deve retornar 404 quando não encontrado")
    void buscarDestinacaoPorId_inexistente_deveRetornar404() throws Exception {
        when(destinacaoService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/destino/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/destino deve criar destinação válida e retornar 201")
    void salvarDestinacao_dadosValidos_deveRetornar201() throws Exception {
        Destinacao entrada = new Destinacao(null, "ORGANICO", "Biodigestor Leste", new Date(), 200.0);
        Destinacao salva   = new Destinacao(1L,   "ORGANICO", "Biodigestor Leste", new Date(), 200.0);
        when(destinacaoService.salvar(any(Destinacao.class))).thenReturn(salva);

        mockMvc.perform(post("/api/destino")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.tipoMaterial", is("ORGANICO")))
                .andExpect(jsonPath("$.localDestino", is("Biodigestor Leste")));

        verify(destinacaoService).salvar(any(Destinacao.class));
    }

    @Test
    @DisplayName("POST /api/destino sem tipo de material deve retornar 400")
    void salvarDestinacao_semTipoMaterial_deveRetornar400() throws Exception {
        when(destinacaoService.salvar(any(Destinacao.class)))
                .thenThrow(new InvalidDataException("Tipo de material é obrigatório"));

        mockMvc.perform(post("/api/destino")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localDestino\": \"Recicladora\", \"quantidadeKg\": 100.0, \"dataRegistro\": 1700000000000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")))
                .andExpect(jsonPath("$.message", containsString("Tipo de material é obrigatório")));
    }

    @Test
    @DisplayName("POST /api/destino com quantidade zero deve retornar 400")
    void salvarDestinacao_comQuantidadeZero_deveRetornar400() throws Exception {
        when(destinacaoService.salvar(any(Destinacao.class)))
                .thenThrow(new InvalidDataException("Quantidade em kg deve ser maior que zero"));

        mockMvc.perform(post("/api/destino")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoMaterial\": \"PLASTICO\", \"localDestino\": \"Recicladora\", \"quantidadeKg\": 0, \"dataRegistro\": 1700000000000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")));
    }

    @Test
    @DisplayName("PUT /api/destino/{id} deve atualizar e retornar 200")
    void atualizarDestinacao_existente_deveRetornar200() throws Exception {
        Destinacao atualizada = new Destinacao(1L, "METAL", "Siderúrgica Norte", new Date(), 500.0);
        when(destinacaoService.atualizar(eq(1L), any(Destinacao.class))).thenReturn(atualizada);

        mockMvc.perform(put("/api/destino/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoMaterial\": \"METAL\", \"localDestino\": \"Siderúrgica Norte\", \"quantidadeKg\": 500.0, \"dataRegistro\": 1700000000000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.tipoMaterial", is("METAL")));
    }

    @Test
    @DisplayName("PUT /api/destino/{id} deve retornar 404 quando não encontrado")
    void atualizarDestinacao_inexistente_deveRetornar404() throws Exception {
        when(destinacaoService.atualizar(eq(999L), any(Destinacao.class)))
                .thenThrow(new ResourceNotFoundException("Destinação não encontrada com ID: 999"));

        mockMvc.perform(put("/api/destino/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoMaterial\": \"VIDRO\", \"localDestino\": \"X\", \"quantidadeKg\": 10.0, \"dataRegistro\": 1700000000000}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }

    @Test
    @DisplayName("DELETE /api/destino/{id} deve excluir e retornar 204")
    void excluirDestinacao_existente_deveRetornar204() throws Exception {
        doNothing().when(destinacaoService).excluir(1L);

        mockMvc.perform(delete("/api/destino/1"))
                .andExpect(status().isNoContent());

        verify(destinacaoService).excluir(1L);
    }

    @Test
    @DisplayName("DELETE /api/destino/{id} deve retornar 404 quando não encontrado")
    void excluirDestinacao_inexistente_deveRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Destinação não encontrada com ID: 999"))
                .when(destinacaoService).excluir(999L);

        mockMvc.perform(delete("/api/destino/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }
}
