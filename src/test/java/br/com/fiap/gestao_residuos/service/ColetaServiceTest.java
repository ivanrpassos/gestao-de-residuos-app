package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Coleta;
import br.com.fiap.gestao_residuos.repository.ColetaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColetaServiceTest {

    @Mock
    private ColetaRepository coletaRepository;

    @InjectMocks
    private ColetaService coletaService;

    @Test
    void deveListarTodasAsColetas() {

        when(coletaRepository.findAll())
                .thenReturn(List.of(new Coleta()));

        List<Coleta> resultado = coletaService.listarTodos();

        assertFalse(resultado.isEmpty());
    }

    @Test
    void deveBuscarColetaPorId() {

        Coleta coleta = new Coleta();

        when(coletaRepository.findById(1L))
                .thenReturn(Optional.of(coleta));

        Optional<Coleta> resultado =
                coletaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void deveLancarErroQuandoIdForInvalido() {

        assertThrows(InvalidDataException.class, () -> {
            coletaService.buscarPorId(0L);
        });
    }

    @Test
    void deveSalvarColeta() {

        Coleta coleta = new Coleta();

        coleta.setStatus("PENDENTE");
        coleta.setDataAgendada(new Date());

        when(coletaRepository.save(coleta))
                .thenReturn(coleta);

        Coleta resultado = coletaService.salvar(coleta);

        assertNotNull(resultado);
    }

    @Test
    void deveLancarErroQuandoStatusForVazio() {

        Coleta coleta = new Coleta();

        coleta.setStatus("");
        coleta.setDataAgendada(new Date());

        assertThrows(InvalidDataException.class, () -> {
            coletaService.salvar(coleta);
        });
    }

    @Test
    void deveAtualizarColeta() {

        Coleta coletaExistente = new Coleta();
        coletaExistente.setStatus("ANTIGO");

        Coleta coletaAtualizada = new Coleta();
        coletaAtualizada.setStatus("NOVO");

        when(coletaRepository.findById(1L))
                .thenReturn(Optional.of(coletaExistente));

        when(coletaRepository.save(any(Coleta.class)))
                .thenReturn(coletaExistente);

        Coleta resultado =
                coletaService.atualizar(1L, coletaAtualizada);

        assertEquals("NOVO", resultado.getStatus());
    }

    @Test
    void deveLancarErroQuandoColetaNaoExistir() {

        when(coletaRepository.findById(1L))
                .thenReturn(Optional.empty());

        Coleta coleta = new Coleta();

        assertThrows(ResourceNotFoundException.class, () -> {
            coletaService.atualizar(1L, coleta);
        });
    }

    @Test
    void deveExcluirColeta() {

        when(coletaRepository.existsById(1L))
                .thenReturn(true);

        doNothing().when(coletaRepository)
                .deleteById(1L);

        assertDoesNotThrow(() -> {
            coletaService.excluir(1L);
        });
    }

    @Test
    void deveLancarErroAoExcluirColetaInexistente() {

        when(coletaRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            coletaService.excluir(1L);
        });
    }
}