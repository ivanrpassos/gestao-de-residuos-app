package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.config.JacksonConfig;
import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Rota;
import br.com.fiap.gestao_residuos.service.RotaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RotaController.class)
@Import(JacksonConfig.class)
@DisplayName("RotaController - Testes Unitários")
public class RotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RotaService rotaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/rotas deve retornar lista com status 200")
    void listarRotas_deveRetornar200ComLista() throws Exception {
        Rota r1 = new Rota(1L, "Caminhão 01", "Av. Central, 100", 2000.0, null);
        Rota r2 = new Rota(2L, "Caminhão 02", "Rua Norte, 50",    1500.0, null);
        when(rotaService.listarTodos()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/rotas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].veiculo", is("Caminhão 01")))
                .andExpect(jsonPath("$[0].enderecoBase", is("Av. Central, 100")))
                .andExpect(jsonPath("$[1].veiculo", is("Caminhão 02")));

        verify(rotaService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/rotas deve retornar lista vazia com status 200")
    void listarRotas_semRegistros_deveRetornar200Vazio() throws Exception {
        when(rotaService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/rotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/rota/{id} deve retornar rota com status 200")
    void buscarRotaPorId_existente_deveRetornar200() throws Exception {
        Rota rota = new Rota(1L, "Caminhão 01", "Av. Central, 100", 2000.0, null);
        when(rotaService.buscarPorId(1L)).thenReturn(Optional.of(rota));

        mockMvc.perform(get("/api/rota/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.veiculo", is("Caminhão 01")))
                .andExpect(jsonPath("$.enderecoBase", is("Av. Central, 100")))
                .andExpect(jsonPath("$.capacidade", is(2000.0)));

        verify(rotaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/rota/{id} deve retornar 404 quando não encontrado")
    void buscarRotaPorId_inexistente_deveRetornar404() throws Exception {
        when(rotaService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rota/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/rota deve criar rota válida e retornar 201")
    void salvarRota_dadosValidos_deveRetornar201() throws Exception {
        Rota entrada = new Rota(null, "Van 03", "Rua Sul, 200", 800.0, null);
        Rota salva   = new Rota(1L,  "Van 03", "Rua Sul, 200", 800.0, null);
        when(rotaService.salvar(any(Rota.class))).thenReturn(salva);

        mockMvc.perform(post("/api/rota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.veiculo", is("Van 03")))
                .andExpect(jsonPath("$.enderecoBase", is("Rua Sul, 200")))
                .andExpect(jsonPath("$.capacidade", is(800.0)));

        verify(rotaService).salvar(any(Rota.class));
    }

    @Test
    @DisplayName("POST /api/rota sem veículo deve retornar 400")
    void salvarRota_semVeiculo_deveRetornar400() throws Exception {
        when(rotaService.salvar(any(Rota.class)))
                .thenThrow(new InvalidDataException("Veículo é obrigatório"));

        mockMvc.perform(post("/api/rota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enderecoBase\": \"Rua Sul, 200\", \"capacidade\": 800.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")))
                .andExpect(jsonPath("$.message", containsString("Veículo é obrigatório")));
    }

    @Test
    @DisplayName("POST /api/rota com capacidade negativa deve retornar 400")
    void salvarRota_comCapacidadeInvalida_deveRetornar400() throws Exception {
        when(rotaService.salvar(any(Rota.class)))
                .thenThrow(new InvalidDataException("Capacidade deve ser maior que zero"));

        mockMvc.perform(post("/api/rota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"veiculo\": \"Van 03\", \"enderecoBase\": \"Rua X\", \"capacidade\": -10.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")));
    }

    @Test
    @DisplayName("PUT /api/rota/{id} deve atualizar e retornar 200")
    void atualizarRota_existente_deveRetornar200() throws Exception {
        Rota atualizada = new Rota(1L, "Caminhão Novo", "Av. Leste, 300", 3000.0, null);
        when(rotaService.atualizar(eq(1L), any(Rota.class))).thenReturn(atualizada);

        mockMvc.perform(put("/api/rota/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"veiculo\": \"Caminhão Novo\", \"enderecoBase\": \"Av. Leste, 300\", \"capacidade\": 3000.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.veiculo", is("Caminhão Novo")))
                .andExpect(jsonPath("$.capacidade", is(3000.0)));
    }

    @Test
    @DisplayName("PUT /api/rota/{id} deve retornar 404 quando não encontrado")
    void atualizarRota_inexistente_deveRetornar404() throws Exception {
        when(rotaService.atualizar(eq(999L), any(Rota.class)))
                .thenThrow(new ResourceNotFoundException("Rota não encontrada com ID: 999"));

        mockMvc.perform(put("/api/rota/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"veiculo\": \"Van X\", \"enderecoBase\": \"Rua Y\", \"capacidade\": 500.0}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }

    @Test
    @DisplayName("DELETE /api/rota/{id} deve excluir e retornar 204")
    void excluirRota_existente_deveRetornar204() throws Exception {
        doNothing().when(rotaService).excluir(1L);

        mockMvc.perform(delete("/api/rota/1"))
                .andExpect(status().isNoContent());

        verify(rotaService).excluir(1L);
    }

    @Test
    @DisplayName("DELETE /api/rota/{id} deve retornar 404 quando não encontrado")
    void excluirRota_inexistente_deveRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Rota não encontrada com ID: 999"))
                .when(rotaService).excluir(999L);

        mockMvc.perform(delete("/api/rota/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }
}
