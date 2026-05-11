package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.config.JacksonConfig;
import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Contenedor;
import br.com.fiap.gestao_residuos.service.ContenedorService;
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

@WebMvcTest(ContenedorController.class)
@Import(JacksonConfig.class)
@DisplayName("ContenedorController - Testes Unitários")
public class ContenedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContenedorService contenedorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/contenedores deve retornar lista com status 200")
    void listarContenedores_deveRetornar200ComLista() throws Exception {
        Contenedor c1 = new Contenedor(1L, "Av. Paulista, 100", 500.0, "PLASTICO");
        Contenedor c2 = new Contenedor(2L, "Rua Augusta, 50", 300.0, "VIDRO");
        when(contenedorService.listarTodos()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/contenedores"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].localizacao", is("Av. Paulista, 100")))
                .andExpect(jsonPath("$[0].tipoMaterial", is("PLASTICO")))
                .andExpect(jsonPath("$[1].tipoMaterial", is("VIDRO")));

        verify(contenedorService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/contenedores deve retornar lista vazia com status 200")
    void listarContenedores_semRegistros_deveRetornar200Vazio() throws Exception {
        when(contenedorService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/contenedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/contenedor/{id} deve retornar contenedor com status 200")
    void buscarContenedorPorId_existente_deveRetornar200() throws Exception {
        Contenedor contenedor = new Contenedor(1L, "Av. Paulista, 100", 500.0, "PLASTICO");
        when(contenedorService.buscarPorId(1L)).thenReturn(Optional.of(contenedor));

        mockMvc.perform(get("/api/contenedor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.localizacao", is("Av. Paulista, 100")))
                .andExpect(jsonPath("$.capacidadeLitros", is(500.0)))
                .andExpect(jsonPath("$.tipoMaterial", is("PLASTICO")));

        verify(contenedorService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/contenedor/{id} deve retornar 404 quando não encontrado")
    void buscarContenedorPorId_inexistente_deveRetornar404() throws Exception {
        when(contenedorService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/contenedor/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/contenedor deve criar contenedor válido e retornar 201")
    void salvarContenedor_dadosValidos_deveRetornar201() throws Exception {
        Contenedor entrada = new Contenedor(null, "Rua Nova, 10", 200.0, "METAL");
        Contenedor salvo   = new Contenedor(1L, "Rua Nova, 10", 200.0, "METAL");
        when(contenedorService.salvar(any(Contenedor.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/contenedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.localizacao", is("Rua Nova, 10")))
                .andExpect(jsonPath("$.tipoMaterial", is("METAL")));

        verify(contenedorService).salvar(any(Contenedor.class));
    }

    @Test
    @DisplayName("POST /api/contenedor sem localização deve retornar 400")
    void salvarContenedor_semLocalizacao_deveRetornar400() throws Exception {
        when(contenedorService.salvar(any(Contenedor.class)))
                .thenThrow(new InvalidDataException("Localização é obrigatória"));

        mockMvc.perform(post("/api/contenedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"capacidadeLitros\": 200.0, \"tipoMaterial\": \"METAL\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")))
                .andExpect(jsonPath("$.message", containsString("Localização é obrigatória")));
    }

    @Test
    @DisplayName("POST /api/contenedor com capacidade zero deve retornar 400")
    void salvarContenedor_comCapacidadeZero_deveRetornar400() throws Exception {
        when(contenedorService.salvar(any(Contenedor.class)))
                .thenThrow(new InvalidDataException("Capacidade em litros deve ser maior que zero"));

        mockMvc.perform(post("/api/contenedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localizacao\": \"Rua X\", \"capacidadeLitros\": 0, \"tipoMaterial\": \"PLASTICO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Dados inválidos")));
    }

    @Test
    @DisplayName("PUT /api/contenedor/{id} deve atualizar e retornar 200")
    void atualizarContenedor_existente_deveRetornar200() throws Exception {
        Contenedor atualizado = new Contenedor(1L, "Rua Atualizada, 99", 600.0, "ORGANICO");
        when(contenedorService.atualizar(eq(1L), any(Contenedor.class))).thenReturn(atualizado);

        mockMvc.perform(put("/api/contenedor/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localizacao\": \"Rua Atualizada, 99\", \"capacidadeLitros\": 600.0, \"tipoMaterial\": \"ORGANICO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.localizacao", is("Rua Atualizada, 99")))
                .andExpect(jsonPath("$.tipoMaterial", is("ORGANICO")));
    }

    @Test
    @DisplayName("PUT /api/contenedor/{id} deve retornar 404 quando não encontrado")
    void atualizarContenedor_inexistente_deveRetornar404() throws Exception {
        when(contenedorService.atualizar(eq(999L), any(Contenedor.class)))
                .thenThrow(new ResourceNotFoundException("Contêiner não encontrado com ID: 999"));

        mockMvc.perform(put("/api/contenedor/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localizacao\": \"Rua X\", \"capacidadeLitros\": 100.0, \"tipoMaterial\": \"VIDRO\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }

    @Test
    @DisplayName("DELETE /api/contenedor/{id} deve excluir e retornar 204")
    void excluirContenedor_existente_deveRetornar204() throws Exception {
        doNothing().when(contenedorService).excluir(1L);

        mockMvc.perform(delete("/api/contenedor/1"))
                .andExpect(status().isNoContent());

        verify(contenedorService).excluir(1L);
    }

    @Test
    @DisplayName("DELETE /api/contenedor/{id} deve retornar 404 quando não encontrado")
    void excluirContenedor_inexistente_deveRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Contêiner não encontrado com ID: 999"))
                .when(contenedorService).excluir(999L);

        mockMvc.perform(delete("/api/contenedor/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")));
    }
}
