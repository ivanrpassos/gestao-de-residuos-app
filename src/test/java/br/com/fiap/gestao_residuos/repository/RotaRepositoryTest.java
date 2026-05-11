package br.com.fiap.gestao_residuos.repository;

import br.com.fiap.gestao_residuos.model.Rota;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RotaRepository - Testes Unitários")
public class RotaRepositoryTest {

    @Mock
    private RotaRepository rotaRepository;

    @Test
    @DisplayName("findAll deve retornar lista de rotas")
    void findAll_deveRetornarListaDeRotas() {
        Rota r1 = new Rota(1L, "Caminhão 01", "Av. Central, 100", 2000.0, null);
        Rota r2 = new Rota(2L, "Caminhão 02", "Rua Norte, 50",    1500.0, null);
        when(rotaRepository.findAll()).thenReturn(List.of(r1, r2));

        List<Rota> resultado = rotaRepository.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getVeiculo()).isEqualTo("Caminhão 01");
        assertThat(resultado.get(0).getEnderecoBase()).isEqualTo("Av. Central, 100");
        assertThat(resultado.get(1).getVeiculo()).isEqualTo("Caminhão 02");
        verify(rotaRepository).findAll();
    }

    @Test
    @DisplayName("findAll deve retornar lista vazia quando não há registros")
    void findAll_semRegistros_deveRetornarListaVazia() {
        when(rotaRepository.findAll()).thenReturn(List.of());

        List<Rota> resultado = rotaRepository.findAll();

        assertThat(resultado).isEmpty();
        verify(rotaRepository).findAll();
    }

    @Test
    @DisplayName("findById deve retornar Optional com rota quando ID existente")
    void findById_idExistente_deveRetornarRota() {
        Rota rota = new Rota(1L, "Caminhão 01", "Av. Central, 100", 2000.0, null);
        when(rotaRepository.findById(1L)).thenReturn(Optional.of(rota));

        Optional<Rota> resultado = rotaRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getVeiculo()).isEqualTo("Caminhão 01");
        assertThat(resultado.get().getCapacidade()).isEqualTo(2000.0);
        verify(rotaRepository).findById(1L);
    }

    @Test
    @DisplayName("findById deve retornar Optional vazio quando ID não encontrado")
    void findById_idInexistente_deveRetornarOptionalVazio() {
        when(rotaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Rota> resultado = rotaRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(rotaRepository).findById(999L);
    }

    @Test
    @DisplayName("save deve persistir e retornar rota salva com ID gerado")
    void save_deveRetornarRotaSalva() {
        Rota entrada = new Rota(null, "Van 03", "Rua Sul, 200", 800.0, null);
        Rota salva   = new Rota(1L,  "Van 03", "Rua Sul, 200", 800.0, null);
        when(rotaRepository.save(entrada)).thenReturn(salva);

        Rota resultado = rotaRepository.save(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getVeiculo()).isEqualTo("Van 03");
        assertThat(resultado.getEnderecoBase()).isEqualTo("Rua Sul, 200");
        assertThat(resultado.getCapacidade()).isEqualTo(800.0);
        verify(rotaRepository).save(entrada);
    }

    @Test
    @DisplayName("existsById deve retornar true quando rota existe")
    void existsById_rotaExistente_deveRetornarTrue() {
        when(rotaRepository.existsById(1L)).thenReturn(true);

        boolean existe = rotaRepository.existsById(1L);

        assertThat(existe).isTrue();
        verify(rotaRepository).existsById(1L);
    }

    @Test
    @DisplayName("existsById deve retornar false quando rota não existe")
    void existsById_rotaInexistente_deveRetornarFalse() {
        when(rotaRepository.existsById(999L)).thenReturn(false);

        boolean existe = rotaRepository.existsById(999L);

        assertThat(existe).isFalse();
        verify(rotaRepository).existsById(999L);
    }

    @Test
    @DisplayName("deleteById deve executar exclusão sem lançar exceção")
    void deleteById_deveExcluirSemExcecao() {
        doNothing().when(rotaRepository).deleteById(1L);

        rotaRepository.deleteById(1L);

        verify(rotaRepository).deleteById(1L);
    }
}
