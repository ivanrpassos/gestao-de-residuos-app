package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Coleta;
import br.com.fiap.gestao_residuos.repository.ColetaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ColetaService - Testes Unitários")
public class ColetaServiceTest {

    @Mock
    private ColetaRepository coletaRepository;

    @InjectMocks
    private ColetaService coletaService;

    @Test
    @DisplayName("listarTodos deve retornar lista de coletas do repositório")
    void listarTodos_deveRetornarListaDeColetas() {
        Coleta c1 = new Coleta(1L, null, null, new Date(), "AGENDADO");
        Coleta c2 = new Coleta(2L, null, null, new Date(), "CONCLUIDO");
        when(coletaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Coleta> resultado = coletaService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getStatus()).isEqualTo("AGENDADO");
        assertThat(resultado.get(1).getStatus()).isEqualTo("CONCLUIDO");
        verify(coletaRepository).findAll();
    }

    @Test
    @DisplayName("listarTodos deve retornar lista vazia quando não há coletas")
    void listarTodos_semRegistros_deveRetornarListaVazia() {
        when(coletaRepository.findAll()).thenReturn(List.of());

        List<Coleta> resultado = coletaService.listarTodos();

        assertThat(resultado).isEmpty();
        verify(coletaRepository).findAll();
    }

    @Test
    @DisplayName("buscarPorId deve retornar Optional com coleta quando ID válido e existente")
    void buscarPorId_idValido_deveRetornarColeta() {
        Coleta coleta = new Coleta(1L, null, null, new Date(), "AGENDADO");
        when(coletaRepository.findById(1L)).thenReturn(Optional.of(coleta));

        Optional<Coleta> resultado = coletaService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getStatus()).isEqualTo("AGENDADO");
        verify(coletaRepository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId deve retornar Optional vazio quando ID não encontrado")
    void buscarPorId_idInexistente_deveRetornarOptionalVazio() {
        when(coletaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Coleta> resultado = coletaService.buscarPorId(999L);

        assertThat(resultado).isEmpty();
        verify(coletaRepository).findById(999L);
    }

    @Test
    @DisplayName("buscarPorId com ID nulo deve lançar InvalidDataException")
    void buscarPorId_idNulo_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> coletaService.buscarPorId(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(coletaRepository);
    }

    @Test
    @DisplayName("buscarPorId com ID zero deve lançar InvalidDataException")
    void buscarPorId_idZero_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> coletaService.buscarPorId(0L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(coletaRepository);
    }

    @Test
    @DisplayName("buscarPorId com ID negativo deve lançar InvalidDataException")
    void buscarPorId_idNegativo_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> coletaService.buscarPorId(-5L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(coletaRepository);
    }

    @Test
    @DisplayName("salvar deve persistir e retornar coleta com dados válidos")
    void salvar_dadosValidos_deveRetornarColetaSalva() {
        Coleta entrada = new Coleta(null, null, null, new Date(), "AGENDADO");
        Coleta salva   = new Coleta(1L,   null, null, new Date(), "AGENDADO");
        when(coletaRepository.save(any(Coleta.class))).thenReturn(salva);

        Coleta resultado = coletaService.salvar(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getStatus()).isEqualTo("AGENDADO");
        verify(coletaRepository).save(any(Coleta.class));
    }

    @Test
    @DisplayName("salvar com coleta nula deve lançar InvalidDataException")
    void salvar_coletaNula_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> coletaService.salvar(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Coleta não pode ser nula");

        verifyNoInteractions(coletaRepository);
    }

    @Test
    @DisplayName("salvar sem data agendada deve lançar InvalidDataException")
    void salvar_semDataAgendada_deveLancarInvalidDataException() {
        Coleta coleta = new Coleta(null, null, null, null, "AGENDADO");

        assertThatThrownBy(() -> coletaService.salvar(coleta))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Data agendada é obrigatória");

        verifyNoInteractions(coletaRepository);
    }

    @Test
    @DisplayName("salvar sem status deve lançar InvalidDataException")
    void salvar_semStatus_deveLancarInvalidDataException() {
        Coleta coleta = new Coleta(null, null, null, new Date(), null);

        assertThatThrownBy(() -> coletaService.salvar(coleta))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Status é obrigatório");

        verifyNoInteractions(coletaRepository);
    }

    @Test
    @DisplayName("salvar com status em branco deve lançar InvalidDataException")
    void salvar_statusEmBranco_deveLancarInvalidDataException() {
        Coleta coleta = new Coleta(null, null, null, new Date(), "   ");

        assertThatThrownBy(() -> coletaService.salvar(coleta))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Status é obrigatório");

        verifyNoInteractions(coletaRepository);
    }

    @Test
    @DisplayName("atualizar deve modificar status e retornar coleta atualizada")
    void atualizar_dadosValidos_deveRetornarColetaAtualizada() {
        Coleta existente  = new Coleta(1L, null, null, new Date(), "AGENDADO");
        Coleta param      = new Coleta(null, null, null, new Date(), "CONCLUIDO");
        Coleta atualizada = new Coleta(1L, null, null, new Date(), "CONCLUIDO");
        when(coletaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(coletaRepository.save(any(Coleta.class))).thenReturn(atualizada);

        Coleta resultado = coletaService.atualizar(1L, param);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getStatus()).isEqualTo("CONCLUIDO");
        verify(coletaRepository).findById(1L);
        verify(coletaRepository).save(any(Coleta.class));
    }

    @Test
    @DisplayName("atualizar com ID inexistente deve lançar ResourceNotFoundException")
    void atualizar_idInexistente_deveLancarResourceNotFoundException() {
        when(coletaRepository.findById(999L)).thenReturn(Optional.empty());

        Coleta param = new Coleta(null, null, null, new Date(), "CONCLUIDO");

        assertThatThrownBy(() -> coletaService.atualizar(999L, param))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("atualizar com ID inválido deve lançar InvalidDataException")
    void atualizar_idInvalido_deveLancarInvalidDataException() {
        Coleta param = new Coleta(null, null, null, new Date(), "CONCLUIDO");

        assertThatThrownBy(() -> coletaService.atualizar(0L, param))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(coletaRepository);
    }

    @Test
    @DisplayName("atualizar com dados nulos deve lançar InvalidDataException")
    void atualizar_dadosNulos_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> coletaService.atualizar(1L, null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Dados da coleta não podem ser nulos");

        verifyNoInteractions(coletaRepository);
    }

    @Test
    @DisplayName("excluir deve remover coleta quando ID existente")
    void excluir_idExistente_deveRemoverColeta() {
        when(coletaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(coletaRepository).deleteById(1L);

        coletaService.excluir(1L);

        verify(coletaRepository).existsById(1L);
        verify(coletaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("excluir com ID inexistente deve lançar ResourceNotFoundException")
    void excluir_idInexistente_deveLancarResourceNotFoundException() {
        when(coletaRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> coletaService.excluir(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(coletaRepository).existsById(999L);
        verify(coletaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("excluir com ID inválido deve lançar InvalidDataException")
    void excluir_idInvalido_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> coletaService.excluir(0L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(coletaRepository);
    }
}
