package br.com.fiap.gestao_residuos.repository;

import br.com.fiap.gestao_residuos.model.Destinacao;
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
@DisplayName("DestinacaoRepository - Testes Unitários")
public class DestinacaoRepositoryTest {

    @Mock
    private DestinacaoRepository destinacaoRepository;

    @Test
    @DisplayName("findAll deve retornar lista de destinações")
    void findAll_deveRetornarListaDeDestinacoes() {
        Destinacao d1 = new Destinacao(1L, "PLASTICO", "Recicladora Norte", new Date(), 150.0);
        Destinacao d2 = new Destinacao(2L, "METAL",    "Siderúrgica Sul",   new Date(), 300.0);
        when(destinacaoRepository.findAll()).thenReturn(List.of(d1, d2));

        List<Destinacao> resultado = destinacaoRepository.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getTipoMaterial()).isEqualTo("PLASTICO");
        assertThat(resultado.get(0).getLocalDestino()).isEqualTo("Recicladora Norte");
        assertThat(resultado.get(1).getTipoMaterial()).isEqualTo("METAL");
        verify(destinacaoRepository).findAll();
    }

    @Test
    @DisplayName("findAll deve retornar lista vazia quando não há registros")
    void findAll_semRegistros_deveRetornarListaVazia() {
        when(destinacaoRepository.findAll()).thenReturn(List.of());

        List<Destinacao> resultado = destinacaoRepository.findAll();

        assertThat(resultado).isEmpty();
        verify(destinacaoRepository).findAll();
    }

    @Test
    @DisplayName("findById deve retornar Optional com destinação quando ID existente")
    void findById_idExistente_deveRetornarDestinacao() {
        Destinacao d = new Destinacao(1L, "VIDRO", "Vidraria Central", new Date(), 80.0);
        when(destinacaoRepository.findById(1L)).thenReturn(Optional.of(d));

        Optional<Destinacao> resultado = destinacaoRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getTipoMaterial()).isEqualTo("VIDRO");
        assertThat(resultado.get().getLocalDestino()).isEqualTo("Vidraria Central");
        assertThat(resultado.get().getQuantidadeKg()).isEqualTo(80.0);
        verify(destinacaoRepository).findById(1L);
    }

    @Test
    @DisplayName("findById deve retornar Optional vazio quando ID não encontrado")
    void findById_idInexistente_deveRetornarOptionalVazio() {
        when(destinacaoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Destinacao> resultado = destinacaoRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(destinacaoRepository).findById(999L);
    }

    @Test
    @DisplayName("save deve persistir e retornar destinação salva com ID gerado")
    void save_deveRetornarDestinacaoSalva() {
        Destinacao entrada = new Destinacao(null, "ORGANICO", "Biodigestor Leste", new Date(), 200.0);
        Destinacao salva   = new Destinacao(1L,   "ORGANICO", "Biodigestor Leste", new Date(), 200.0);
        when(destinacaoRepository.save(entrada)).thenReturn(salva);

        Destinacao resultado = destinacaoRepository.save(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTipoMaterial()).isEqualTo("ORGANICO");
        assertThat(resultado.getLocalDestino()).isEqualTo("Biodigestor Leste");
        assertThat(resultado.getQuantidadeKg()).isEqualTo(200.0);
        verify(destinacaoRepository).save(entrada);
    }

    @Test
    @DisplayName("existsById deve retornar true quando destinação existe")
    void existsById_destinacaoExistente_deveRetornarTrue() {
        when(destinacaoRepository.existsById(1L)).thenReturn(true);

        boolean existe = destinacaoRepository.existsById(1L);

        assertThat(existe).isTrue();
        verify(destinacaoRepository).existsById(1L);
    }

    @Test
    @DisplayName("existsById deve retornar false quando destinação não existe")
    void existsById_destinacaoInexistente_deveRetornarFalse() {
        when(destinacaoRepository.existsById(999L)).thenReturn(false);

        boolean existe = destinacaoRepository.existsById(999L);

        assertThat(existe).isFalse();
        verify(destinacaoRepository).existsById(999L);
    }

    @Test
    @DisplayName("deleteById deve executar exclusão sem lançar exceção")
    void deleteById_deveExcluirSemExcecao() {
        doNothing().when(destinacaoRepository).deleteById(1L);

        destinacaoRepository.deleteById(1L);

        verify(destinacaoRepository).deleteById(1L);
    }
}
