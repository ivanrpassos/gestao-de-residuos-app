package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.config.JacksonConfig;
import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Contenedor;
import br.com.fiap.gestao_residuos.model.Leitura;
import br.com.fiap.gestao_residuos.service.LeituraService;
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

@WebMvcTest(LeituraController.class)
@Import(JacksonConfig.class)
@DisplayName("LeituraController - Testes Unitários")
public class LeituraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeituraService leituraService;

    @Autowired
    private ObjectMapper objectMapper;

    private Contenedor contenedorFixture() {
        return new Contenedor(1L, "Av. Paulista, 100", 500.0, "PLASTICO");
    }

    @Test
    @DisplayName("GET /api/leituras deve retornar lista com status 200")
    void listarLeituras_deveRetornar200ComLista() throws Exception {
        Contenedor c = contenedorFixture();
        Leitura l1 = new Leitura(1L, c, new Date(), 75.0, 120.5);
        Leitura l2 = new Leitura(2L, c, new Date(), 40.0,  60.0);
        when(leituraService.listarTodos()).thenReturn(List.of(l1, l2));

        mockMvc.perform(get("/api/leituras"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nivelPercent", is(75.0)))
                .andExpect(jsonPath("$[0].pesoKg", is(120.5)))
                .andExpect(jsonPath("$[1].nivelPercent", is(40.0)));

        verify(leituraService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/leituras deve retornar lista vazia com status 200")
    void listarLeituras_semRegistros_deveRetornar200Vazio() throws Exception {
        when(leituraService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/leituras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/leitura/{id} deve retornar leitura com status 200")
    void buscarLeituraPorId_existente_deveRetornar200() throws Exception {
        Leitura leitura = new Leitura(1L, contenedorFixture(), new Date(), 85.0, 200.0);
        when(leituraService.buscarPorId(1L)).thenReturn(Optional.of(leitura));

        mockMvc.perform(get("/api/leitura/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nivelPercent", is(85.0)))
                .andExpect(jsonPath("$.pesoKg", is(200.0)));

        verify(leituraService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/leitura/{id} deve retornar 404 quando não encontrado")
    void buscarLeituraPorId_inexistente_deveRetornar404() throws Exception {
        when(leituraService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/leitura/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/leitura deve criar leitura válida e retornar 201")
    void salvarLeitura_dadosValidos_deveRetornar201() throws Exception {
        Contenedor c = contenedorFixture();
        Leitura salva = new Leitura(1L, c, new Date(), 60.0, 90.0);
        when(leituraService.salvar(any(Leitura.class))).thenReturn(salva);

        mockMvc.perform(post("/api/leitura")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contenedor\": {\"id\": 1}, \"dataHora\": 1700000000000, \"nivelPercent\": 60.0, \"pesoKg\": 90.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nivelPercent", is(60.0)))
                .andExpect(jsonPath("$.pesoKg", is(90.0)));

        verify(leituraService).salvar(any(Leitura.class));
    }

    @Test
    @DisplayName("POST /api/leitura sem contenedor deve retornar 400")
    void salvarLeitura_semContenedor_deveRetornar400() throws Exception {
        when(leituraService.salvar(any(Leitura.class)))
                .thenThrow(new InvalidDataException("Contêiner é obrigatório para a leitura"));

        mockMvc.perform(post("/api/leitura")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataHora\": 1700000000000, \"nivelPercent\": 60.0, \"pesoKg\": 90.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")))
                .andExpect(jsonPath("$.message", containsString("Contêiner é obrigatório")));
    }

    @Test
    @DisplayName("POST /api/leitura com nível percentual acima de 100 deve retornar 400")
    void salvarLeitura_nivelAcimaDe100_deveRetornar400() throws Exception {
        when(leituraService.salvar(any(Leitura.class)))
                .thenThrow(new InvalidDataException("Nível percentual deve estar entre 0 e 100"));

        mockMvc.perform(post("/api/leitura")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contenedor\": {\"id\": 1}, \"dataHora\": 1700000000000, \"nivelPercent\": 150.0, \"pesoKg\": 50.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")));
    }

    @Test
    @DisplayName("PUT /api/leitura/{id} deve atualizar e retornar 200")
    void atualizarLeitura_existente_deveRetornar200() throws Exception {
        Leitura atualizada = new Leitura(1L, contenedorFixture(), new Date(), 95.0, 250.0);
        when(leituraService.atualizar(eq(1L), any(Leitura.class))).thenReturn(atualizada);

        mockMvc.perform(put("/api/leitura/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nivelPercent\": 95.0, \"pesoKg\": 250.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nivelPercent", is(95.0)));
    }

    @Test
    @DisplayName("PUT /api/leitura/{id} deve retornar 404 quando não encontrado")
    void atualizarLeitura_inexistente_deveRetornar404() throws Exception {
        when(leituraService.atualizar(eq(999L), any(Leitura.class)))
                .thenThrow(new ResourceNotFoundException("Leitura não encontrada com ID: 999"));

        mockMvc.perform(put("/api/leitura/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nivelPercent\": 50.0, \"pesoKg\": 100.0}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }

    @Test
    @DisplayName("DELETE /api/leitura/{id} deve excluir e retornar 204")
    void excluirLeitura_existente_deveRetornar204() throws Exception {
        doNothing().when(leituraService).excluir(1L);

        mockMvc.perform(delete("/api/leitura/1"))
                .andExpect(status().isNoContent());

        verify(leituraService).excluir(1L);
    }

    @Test
    @DisplayName("DELETE /api/leitura/{id} deve retornar 404 quando não encontrado")
    void excluirLeitura_inexistente_deveRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Leitura não encontrada com ID: 999"))
                .when(leituraService).excluir(999L);

        mockMvc.perform(delete("/api/leitura/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }
}
