package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Rota;
import br.com.fiap.gestao_residuos.repository.RotaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RotaService - Testes Unitários")
public class RotaServiceTest {

    @Mock
    private RotaRepository rotaRepository;

    @InjectMocks
    private RotaService rotaService;

    @Test
    @DisplayName("listarTodos deve retornar lista de rotas")
    void listarTodos_deveRetornarLista() {
        Rota r1 = new Rota(1L, "Caminhão 01", "Av. Central, 100", 2000.0, null);
        Rota r2 = new Rota(2L, "Caminhão 02", "Rua Norte, 50",    1500.0, null);
        when(rotaRepository.findAll()).thenReturn(List.of(r1, r2));

        List<Rota> resultado = rotaService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getVeiculo()).isEqualTo("Caminhão 01");
        assertThat(resultado.get(1).getVeiculo()).isEqualTo("Caminhão 02");
        verify(rotaRepository).findAll();
    }

    @Test
    @DisplayName("listarTodos deve retornar lista vazia quando não há rotas")
    void listarTodos_semRegistros_deveRetornarListaVazia() {
        when(rotaRepository.findAll()).thenReturn(List.of());

        List<Rota> resultado = rotaService.listarTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId deve retornar rota quando ID válido e existente")
    void buscarPorId_idValido_deveRetornarRota() {
        Rota rota = new Rota(1L, "Caminhão 01", "Av. Central, 100", 2000.0, null);
        when(rotaRepository.findById(1L)).thenReturn(Optional.of(rota));

        Optional<Rota> resultado = rotaService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getVeiculo()).isEqualTo("Caminhão 01");
        assertThat(resultado.get().getCapacidade()).isEqualTo(2000.0);
        verify(rotaRepository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId deve retornar Optional vazio para ID inexistente")
    void buscarPorId_idInexistente_deveRetornarOptionalVazio() {
        when(rotaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Rota> resultado = rotaService.buscarPorId(999L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId com ID nulo deve lançar InvalidDataException")
    void buscarPorId_idNulo_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> rotaService.buscarPorId(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("buscarPorId com ID zero deve lançar InvalidDataException")
    void buscarPorId_idZero_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> rotaService.buscarPorId(0L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("salvar deve persistir e retornar rota com dados válidos")
    void salvar_dadosValidos_deveRetornarRotaSalva() {
        Rota entrada = new Rota(null, "Van 03", "Rua Sul, 200", 800.0, null);
        Rota salva   = new Rota(1L,  "Van 03", "Rua Sul, 200", 800.0, null);
        when(rotaRepository.save(any(Rota.class))).thenReturn(salva);

        Rota resultado = rotaService.salvar(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getVeiculo()).isEqualTo("Van 03");
        assertThat(resultado.getEnderecoBase()).isEqualTo("Rua Sul, 200");
        assertThat(resultado.getCapacidade()).isEqualTo(800.0);
        verify(rotaRepository).save(any(Rota.class));
    }

    @Test
    @DisplayName("salvar com rota nula deve lançar InvalidDataException")
    void salvar_rotaNula_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> rotaService.salvar(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Rota não pode ser nula");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("salvar sem veículo deve lançar InvalidDataException")
    void salvar_semVeiculo_deveLancarInvalidDataException() {
        Rota r = new Rota(null, null, "Rua Sul, 200", 800.0, null);

        assertThatThrownBy(() -> rotaService.salvar(r))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Veículo é obrigatório");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("salvar com veículo em branco deve lançar InvalidDataException")
    void salvar_veiculoEmBranco_deveLancarInvalidDataException() {
        Rota r = new Rota(null, "   ", "Rua Sul, 200", 800.0, null);

        assertThatThrownBy(() -> rotaService.salvar(r))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Veículo é obrigatório");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("salvar sem endereço base deve lançar InvalidDataException")
    void salvar_semEnderecoBase_deveLancarInvalidDataException() {
        Rota r = new Rota(null, "Van 03", null, 800.0, null);

        assertThatThrownBy(() -> rotaService.salvar(r))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Endereço base é obrigatório");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("salvar com capacidade zero deve lançar InvalidDataException")
    void salvar_capacidadeZero_deveLancarInvalidDataException() {
        Rota r = new Rota(null, "Van 03", "Rua Sul, 200", 0.0, null);

        assertThatThrownBy(() -> rotaService.salvar(r))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Capacidade deve ser maior que zero");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("salvar com capacidade negativa deve lançar InvalidDataException")
    void salvar_capacidadeNegativa_deveLancarInvalidDataException() {
        Rota r = new Rota(null, "Van 03", "Rua Sul, 200", -100.0, null);

        assertThatThrownBy(() -> rotaService.salvar(r))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Capacidade deve ser maior que zero");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("atualizar deve modificar campos e retornar rota atualizada")
    void atualizar_dadosValidos_deveRetornarRotaAtualizada() {
        Rota existente  = new Rota(1L, "Caminhão Velho", "Av. Antiga, 1", 1000.0, null);
        Rota param      = new Rota(null, "Caminhão Novo", "Av. Nova, 99", 3000.0, null);
        Rota atualizada = new Rota(1L, "Caminhão Novo", "Av. Nova, 99", 3000.0, null);
        when(rotaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rotaRepository.save(any(Rota.class))).thenReturn(atualizada);

        Rota resultado = rotaService.atualizar(1L, param);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getVeiculo()).isEqualTo("Caminhão Novo");
        assertThat(resultado.getEnderecoBase()).isEqualTo("Av. Nova, 99");
        assertThat(resultado.getCapacidade()).isEqualTo(3000.0);
        verify(rotaRepository).findById(1L);
        verify(rotaRepository).save(any(Rota.class));
    }

    @Test
    @DisplayName("atualizar com ID inexistente deve lançar ResourceNotFoundException")
    void atualizar_idInexistente_deveLancarResourceNotFoundException() {
        when(rotaRepository.findById(999L)).thenReturn(Optional.empty());
        Rota param = new Rota(null, "Van X", "Rua Y", 500.0, null);

        assertThatThrownBy(() -> rotaService.atualizar(999L, param))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("atualizar com ID inválido deve lançar InvalidDataException")
    void atualizar_idInvalido_deveLancarInvalidDataException() {
        Rota param = new Rota(null, "Van X", "Rua Y", 500.0, null);

        assertThatThrownBy(() -> rotaService.atualizar(-1L, param))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("atualizar com dados nulos deve lançar InvalidDataException")
    void atualizar_dadosNulos_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> rotaService.atualizar(1L, null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Dados da rota não podem ser nulos");

        verifyNoInteractions(rotaRepository);
    }

    @Test
    @DisplayName("excluir deve remover rota quando ID existente")
    void excluir_idExistente_deveRemoverRota() {
        when(rotaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rotaRepository).deleteById(1L);

        rotaService.excluir(1L);

        verify(rotaRepository).existsById(1L);
        verify(rotaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("excluir com ID inexistente deve lançar ResourceNotFoundException")
    void excluir_idInexistente_deveLancarResourceNotFoundException() {
        when(rotaRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> rotaService.excluir(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(rotaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("excluir com ID inválido deve lançar InvalidDataException")
    void excluir_idInvalido_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> rotaService.excluir(0L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(rotaRepository);
    }
}
