package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.model.Coleta;
import br.com.fiap.gestao_residuos.service.ColetaService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ColetaController.class)
class ColetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ColetaService coletaService;

    @Test
    void deveListarTodasAsColetas() throws Exception {

        when(coletaService.listarTodos())
                .thenReturn(List.of(new Coleta()));

        mockMvc.perform(get("/api/coletas"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarColetaPorId() throws Exception {

        Coleta coleta = new Coleta();

        when(coletaService.buscarPorId(1L))
                .thenReturn(Optional.of(coleta));

        mockMvc.perform(get("/api/coleta/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar404QuandoColetaNaoExistir() throws Exception {

        when(coletaService.buscarPorId(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/coleta/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveSalvarColeta() throws Exception {

        Coleta coleta = new Coleta();

        when(coletaService.salvar(coleta))
                .thenReturn(coleta);

        mockMvc.perform(post("/api/coleta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coleta)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveAtualizarColeta() throws Exception {

        Coleta coleta = new Coleta();

        when(coletaService.atualizar(1L, coleta))
                .thenReturn(coleta);

        mockMvc.perform(put("/api/coleta/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coleta)))
                .andExpect(status().isOk());
    }

    @Test
    void deveExcluirColeta() throws Exception {

        doNothing().when(coletaService).excluir(1L);

        mockMvc.perform(delete("/api/coleta/1"))
                .andExpect(status().isNoContent());
    }
}