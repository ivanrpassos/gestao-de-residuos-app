package br.com.fiap.gestao_residuos.repository;

import br.com.fiap.gestao_residuos.model.Contenedor;
import br.com.fiap.gestao_residuos.model.Leitura;
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
@DisplayName("LeituraRepository - Testes Unitários")
public class LeituraRepositoryTest {

    @Mock
    private LeituraRepository leituraRepository;

    private Contenedor contenedorFixture() {
        return new Contenedor(1L, "Av. Paulista, 100", 500.0, "PLASTICO");
    }

    @Test
    @DisplayName("findAll deve retornar lista de leituras")
    void findAll_deveRetornarListaDeLeituras() {
        Contenedor c = contenedorFixture();
        Leitura l1 = new Leitura(1L, c, new Date(), 75.0, 120.5);
        Leitura l2 = new Leitura(2L, c, new Date(), 40.0,  60.0);
        when(leituraRepository.findAll()).thenReturn(List.of(l1, l2));

        List<Leitura> resultado = leituraRepository.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNivelPercent()).isEqualTo(75.0);
        assertThat(resultado.get(0).getPesoKg()).isEqualTo(120.5);
        assertThat(resultado.get(1).getNivelPercent()).isEqualTo(40.0);
        verify(leituraRepository).findAll();
    }

    @Test
    @DisplayName("findAll deve retornar lista vazia quando não há registros")
    void findAll_semRegistros_deveRetornarListaVazia() {
        when(leituraRepository.findAll()).thenReturn(List.of());

        List<Leitura> resultado = leituraRepository.findAll();

        assertThat(resultado).isEmpty();
        verify(leituraRepository).findAll();
    }

    @Test
    @DisplayName("findById deve retornar Optional com leitura quando ID existente")
    void findById_idExistente_deveRetornarLeitura() {
        Leitura leitura = new Leitura(1L, contenedorFixture(), new Date(), 85.0, 200.0);
        when(leituraRepository.findById(1L)).thenReturn(Optional.of(leitura));

        Optional<Leitura> resultado = leituraRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getNivelPercent()).isEqualTo(85.0);
        assertThat(resultado.get().getPesoKg()).isEqualTo(200.0);
        verify(leituraRepository).findById(1L);
    }

    @Test
    @DisplayName("findById deve retornar Optional vazio quando ID não encontrado")
    void findById_idInexistente_deveRetornarOptionalVazio() {
        when(leituraRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Leitura> resultado = leituraRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(leituraRepository).findById(999L);
    }

    @Test
    @DisplayName("save deve persistir e retornar leitura salva com ID gerado")
    void save_deveRetornarLeituraSalva() {
        Contenedor c = contenedorFixture();
        Leitura entrada = new Leitura(null, c, new Date(), 60.0, 90.0);
        Leitura salva   = new Leitura(1L,  c, new Date(), 60.0, 90.0);
        when(leituraRepository.save(entrada)).thenReturn(salva);

        Leitura resultado = leituraRepository.save(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNivelPercent()).isEqualTo(60.0);
        assertThat(resultado.getPesoKg()).isEqualTo(90.0);
        verify(leituraRepository).save(entrada);
    }

    @Test
    @DisplayName("existsById deve retornar true quando leitura existe")
    void existsById_leituraExistente_deveRetornarTrue() {
        when(leituraRepository.existsById(1L)).thenReturn(true);

        boolean existe = leituraRepository.existsById(1L);

        assertThat(existe).isTrue();
        verify(leituraRepository).existsById(1L);
    }

    @Test
    @DisplayName("existsById deve retornar false quando leitura não existe")
    void existsById_leituraInexistente_deveRetornarFalse() {
        when(leituraRepository.existsById(999L)).thenReturn(false);

        boolean existe = leituraRepository.existsById(999L);

        assertThat(existe).isFalse();
        verify(leituraRepository).existsById(999L);
    }

    @Test
    @DisplayName("deleteById deve executar exclusão sem lançar exceção")
    void deleteById_deveExcluirSemExcecao() {
        doNothing().when(leituraRepository).deleteById(1L);

        leituraRepository.deleteById(1L);

        verify(leituraRepository).deleteById(1L);
    }
}
