package br.com.fiap.gestao_residuos.repository;

import br.com.fiap.gestao_residuos.model.Coleta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ColetaRepository - Testes Unitários")
public class ColetaRepositoryTest {

    @Mock
    private ColetaRepository coletaRepository;

    @Test
    @DisplayName("findAll deve retornar lista de coletas")
    void findAll_deveRetornarListaDeColetas() {
        Coleta c1 = new Coleta(1L, null, null, new Date(), "AGENDADO");
        Coleta c2 = new Coleta(2L, null, null, new Date(), "CONCLUIDO");
        when(coletaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Coleta> resultado = coletaRepository.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getStatus()).isEqualTo("AGENDADO");
        assertThat(resultado.get(1).getStatus()).isEqualTo("CONCLUIDO");
        verify(coletaRepository).findAll();
    }

    @Test
    @DisplayName("findAll deve retornar lista vazia quando não há registros")
    void findAll_semRegistros_deveRetornarListaVazia() {
        when(coletaRepository.findAll()).thenReturn(List.of());

        List<Coleta> resultado = coletaRepository.findAll();

        assertThat(resultado).isEmpty();
        verify(coletaRepository).findAll();
    }

    @Test
    @DisplayName("findById deve retornar Optional com coleta quando ID existente")
    void findById_idExistente_deveRetornarColeta() {
        Coleta coleta = new Coleta(1L, null, null, new Date(), "AGENDADO");
        when(coletaRepository.findById(1L)).thenReturn(Optional.of(coleta));

        Optional<Coleta> resultado = coletaRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getStatus()).isEqualTo("AGENDADO");
        verify(coletaRepository).findById(1L);
    }

    @Test
    @DisplayName("findById deve retornar Optional vazio quando ID não encontrado")
    void findById_idInexistente_deveRetornarOptionalVazio() {
        when(coletaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Coleta> resultado = coletaRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(coletaRepository).findById(999L);
    }

    @Test
    @DisplayName("save deve persistir e retornar coleta salva")
    void save_deveRetornarColetaSalva() {
        Coleta entrada = new Coleta(null, null, null, new Date(), "AGENDADO");
        Coleta salva   = new Coleta(1L,   null, null, new Date(), "AGENDADO");
        when(coletaRepository.save(entrada)).thenReturn(salva);

        Coleta resultado = coletaRepository.save(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getStatus()).isEqualTo("AGENDADO");
        verify(coletaRepository).save(entrada);
    }

    @Test
    @DisplayName("existsById deve retornar true quando coleta existe")
    void existsById_coletaExistente_deveRetornarTrue() {
        when(coletaRepository.existsById(1L)).thenReturn(true);

        boolean existe = coletaRepository.existsById(1L);

        assertThat(existe).isTrue();
        verify(coletaRepository).existsById(1L);
    }

    @Test
    @DisplayName("existsById deve retornar false quando coleta não existe")
    void existsById_coletaInexistente_deveRetornarFalse() {
        when(coletaRepository.existsById(999L)).thenReturn(false);

        boolean existe = coletaRepository.existsById(999L);

        assertThat(existe).isFalse();
        verify(coletaRepository).existsById(999L);
    }

    @Test
    @DisplayName("deleteById deve executar exclusão sem lançar exceção")
    void deleteById_deveExcluirSemExcecao() {
        doNothing().when(coletaRepository).deleteById(1L);

        coletaRepository.deleteById(1L);

        verify(coletaRepository).deleteById(1L);
    }
}
