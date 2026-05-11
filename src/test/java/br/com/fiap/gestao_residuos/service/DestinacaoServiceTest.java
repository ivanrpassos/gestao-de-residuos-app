package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Destinacao;
import br.com.fiap.gestao_residuos.repository.DestinacaoRepository;
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
@DisplayName("DestinacaoService - Testes Unitários")
public class DestinacaoServiceTest {

    @Mock
    private DestinacaoRepository destinacaoRepository;

    @InjectMocks
    private DestinacaoService destinacaoService;

    @Test
    @DisplayName("listarTodos deve retornar lista de destinações")
    void listarTodos_deveRetornarLista() {
        Destinacao d1 = new Destinacao(1L, "PLASTICO", "Recicladora Norte", new Date(), 150.0);
        Destinacao d2 = new Destinacao(2L, "METAL",    "Siderúrgica Sul",   new Date(), 300.0);
        when(destinacaoRepository.findAll()).thenReturn(List.of(d1, d2));

        List<Destinacao> resultado = destinacaoService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getTipoMaterial()).isEqualTo("PLASTICO");
        assertThat(resultado.get(1).getTipoMaterial()).isEqualTo("METAL");
        verify(destinacaoRepository).findAll();
    }

    @Test
    @DisplayName("buscarPorId deve retornar destinação quando ID válido e existente")
    void buscarPorId_idValido_deveRetornarDestinacao() {
        Destinacao d = new Destinacao(1L, "VIDRO", "Vidraria Central", new Date(), 80.0);
        when(destinacaoRepository.findById(1L)).thenReturn(Optional.of(d));

        Optional<Destinacao> resultado = destinacaoService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTipoMaterial()).isEqualTo("VIDRO");
        verify(destinacaoRepository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId com ID nulo deve lançar InvalidDataException")
    void buscarPorId_idNulo_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> destinacaoService.buscarPorId(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(destinacaoRepository);
    }

    @Test
    @DisplayName("buscarPorId com ID negativo deve lançar InvalidDataException")
    void buscarPorId_idNegativo_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> destinacaoService.buscarPorId(-3L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(destinacaoRepository);
    }

    @Test
    @DisplayName("salvar deve persistir e retornar destinação com dados válidos")
    void salvar_dadosValidos_deveRetornarDestinacaoSalva() {
        Destinacao entrada = new Destinacao(null, "ORGANICO", "Biodigestor Leste", new Date(), 200.0);
        Destinacao salva   = new Destinacao(1L,   "ORGANICO", "Biodigestor Leste", new Date(), 200.0);
        when(destinacaoRepository.save(any(Destinacao.class))).thenReturn(salva);

        Destinacao resultado = destinacaoService.salvar(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTipoMaterial()).isEqualTo("ORGANICO");
        assertThat(resultado.getLocalDestino()).isEqualTo("Biodigestor Leste");
        assertThat(resultado.getQuantidadeKg()).isEqualTo(200.0);
        verify(destinacaoRepository).save(any(Destinacao.class));
    }

    @Test
    @DisplayName("salvar com destinação nula deve lançar InvalidDataException")
    void salvar_destinacaoNula_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> destinacaoService.salvar(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Destinação não pode ser nula");

        verifyNoInteractions(destinacaoRepository);
    }

    @Test
    @DisplayName("salvar sem tipo de material deve lançar InvalidDataException")
    void salvar_semTipoMaterial_deveLancarInvalidDataException() {
        Destinacao d = new Destinacao(null, null, "Recicladora", new Date(), 100.0);

        assertThatThrownBy(() -> destinacaoService.salvar(d))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Tipo de material é obrigatório");

        verifyNoInteractions(destinacaoRepository);
    }

    @Test
    @DisplayName("salvar sem local de destino deve lançar InvalidDataException")
    void salvar_semLocalDestino_deveLancarInvalidDataException() {
        Destinacao d = new Destinacao(null, "PLASTICO", null, new Date(), 100.0);

        assertThatThrownBy(() -> destinacaoService.salvar(d))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Local de destino é obrigatório");

        verifyNoInteractions(destinacaoRepository);
    }

    @Test
    @DisplayName("salvar com quantidade zero deve lançar InvalidDataException")
    void salvar_quantidadeZero_deveLancarInvalidDataException() {
        Destinacao d = new Destinacao(null, "PLASTICO", "Recicladora", new Date(), 0.0);

        assertThatThrownBy(() -> destinacaoService.salvar(d))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Quantidade em kg deve ser maior que zero");

        verifyNoInteractions(destinacaoRepository);
    }

    @Test
    @DisplayName("salvar sem data de registro deve lançar InvalidDataException")
    void salvar_semDataRegistro_deveLancarInvalidDataException() {
        Destinacao d = new Destinacao(null, "PLASTICO", "Recicladora", null, 100.0);

        assertThatThrownBy(() -> destinacaoService.salvar(d))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Data de registro é obrigatória");

        verifyNoInteractions(destinacaoRepository);
    }

    @Test
    @DisplayName("atualizar deve modificar campos e retornar destinação atualizada")
    void atualizar_dadosValidos_deveRetornarDestinacaoAtualizada() {
        Destinacao existente  = new Destinacao(1L, "PLASTICO", "Recicladora A", new Date(), 100.0);
        Destinacao param      = new Destinacao(null, "METAL", "Siderúrgica B", new Date(), 500.0);
        Destinacao atualizada = new Destinacao(1L, "METAL", "Siderúrgica B", new Date(), 500.0);
        when(destinacaoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(destinacaoRepository.save(any(Destinacao.class))).thenReturn(atualizada);

        Destinacao resultado = destinacaoService.atualizar(1L, param);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTipoMaterial()).isEqualTo("METAL");
        assertThat(resultado.getLocalDestino()).isEqualTo("Siderúrgica B");
        verify(destinacaoRepository).findById(1L);
        verify(destinacaoRepository).save(any(Destinacao.class));
    }

    @Test
    @DisplayName("atualizar com ID inexistente deve lançar ResourceNotFoundException")
    void atualizar_idInexistente_deveLancarResourceNotFoundException() {
        when(destinacaoRepository.findById(999L)).thenReturn(Optional.empty());
        Destinacao param = new Destinacao(null, "VIDRO", "Vidraria X", new Date(), 50.0);

        assertThatThrownBy(() -> destinacaoService.atualizar(999L, param))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("atualizar com ID inválido deve lançar InvalidDataException")
    void atualizar_idInvalido_deveLancarInvalidDataException() {
        Destinacao param = new Destinacao(null, "VIDRO", "Vidraria X", new Date(), 50.0);

        assertThatThrownBy(() -> destinacaoService.atualizar(0L, param))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(destinacaoRepository);
    }

    @Test
    @DisplayName("atualizar com dados nulos deve lançar InvalidDataException")
    void atualizar_dadosNulos_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> destinacaoService.atualizar(1L, null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Dados da destinação não podem ser nulos");

        verifyNoInteractions(destinacaoRepository);
    }

    @Test
    @DisplayName("excluir deve remover destinação quando ID existente")
    void excluir_idExistente_deveRemoverDestinacao() {
        when(destinacaoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(destinacaoRepository).deleteById(1L);

        destinacaoService.excluir(1L);

        verify(destinacaoRepository).existsById(1L);
        verify(destinacaoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("excluir com ID inexistente deve lançar ResourceNotFoundException")
    void excluir_idInexistente_deveLancarResourceNotFoundException() {
        when(destinacaoRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> destinacaoService.excluir(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(destinacaoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("excluir com ID inválido deve lançar InvalidDataException")
    void excluir_idInvalido_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> destinacaoService.excluir(-2L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(destinacaoRepository);
    }
}
