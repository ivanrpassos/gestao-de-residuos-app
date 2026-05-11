package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Contenedor;
import br.com.fiap.gestao_residuos.model.Leitura;
import br.com.fiap.gestao_residuos.repository.LeituraRepository;
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
@DisplayName("LeituraService - Testes Unitários")
public class LeituraServiceTest {

    @Mock
    private LeituraRepository leituraRepository;

    @InjectMocks
    private LeituraService leituraService;

    private Contenedor contenedorFixture() {
        return new Contenedor(1L, "Av. Paulista, 100", 500.0, "PLASTICO");
    }

    @Test
    @DisplayName("listarTodos deve retornar lista de leituras")
    void listarTodos_deveRetornarLista() {
        Contenedor c = contenedorFixture();
        Leitura l1 = new Leitura(1L, c, new Date(), 75.0, 120.5);
        Leitura l2 = new Leitura(2L, c, new Date(), 40.0,  60.0);
        when(leituraRepository.findAll()).thenReturn(List.of(l1, l2));

        List<Leitura> resultado = leituraService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNivelPercent()).isEqualTo(75.0);
        assertThat(resultado.get(1).getNivelPercent()).isEqualTo(40.0);
        verify(leituraRepository).findAll();
    }

    @Test
    @DisplayName("buscarPorId deve retornar leitura quando ID válido e existente")
    void buscarPorId_idValido_deveRetornarLeitura() {
        Leitura leitura = new Leitura(1L, contenedorFixture(), new Date(), 85.0, 200.0);
        when(leituraRepository.findById(1L)).thenReturn(Optional.of(leitura));

        Optional<Leitura> resultado = leituraService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNivelPercent()).isEqualTo(85.0);
        verify(leituraRepository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId com ID nulo deve lançar InvalidDataException")
    void buscarPorId_idNulo_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> leituraService.buscarPorId(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("buscarPorId com ID zero deve lançar InvalidDataException")
    void buscarPorId_idZero_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> leituraService.buscarPorId(0L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("salvar deve persistir e retornar leitura com dados válidos")
    void salvar_dadosValidos_deveRetornarLeituraSalva() {
        Contenedor c = contenedorFixture();
        Leitura entrada = new Leitura(null, c, new Date(), 60.0, 90.0);
        Leitura salva   = new Leitura(1L,  c, new Date(), 60.0, 90.0);
        when(leituraRepository.save(any(Leitura.class))).thenReturn(salva);

        Leitura resultado = leituraService.salvar(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNivelPercent()).isEqualTo(60.0);
        assertThat(resultado.getPesoKg()).isEqualTo(90.0);
        verify(leituraRepository).save(any(Leitura.class));
    }

    @Test
    @DisplayName("salvar com leitura nula deve lançar InvalidDataException")
    void salvar_leituraNula_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> leituraService.salvar(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Leitura não pode ser nula");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("salvar sem contenedor deve lançar InvalidDataException")
    void salvar_semContenedor_deveLancarInvalidDataException() {
        Leitura l = new Leitura(null, null, new Date(), 60.0, 90.0);

        assertThatThrownBy(() -> leituraService.salvar(l))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Contêiner é obrigatório");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("salvar sem data e hora deve lançar InvalidDataException")
    void salvar_semDataHora_deveLancarInvalidDataException() {
        Leitura l = new Leitura(null, contenedorFixture(), null, 60.0, 90.0);

        assertThatThrownBy(() -> leituraService.salvar(l))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Data e hora são obrigatórias");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("salvar com nível percentual acima de 100 deve lançar InvalidDataException")
    void salvar_nivelAcimaDe100_deveLancarInvalidDataException() {
        Leitura l = new Leitura(null, contenedorFixture(), new Date(), 150.0, 90.0);

        assertThatThrownBy(() -> leituraService.salvar(l))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Nível percentual deve estar entre 0 e 100");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("salvar com nível percentual negativo deve lançar InvalidDataException")
    void salvar_nivelNegativo_deveLancarInvalidDataException() {
        Leitura l = new Leitura(null, contenedorFixture(), new Date(), -10.0, 90.0);

        assertThatThrownBy(() -> leituraService.salvar(l))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Nível percentual deve estar entre 0 e 100");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("salvar com peso negativo deve lançar InvalidDataException")
    void salvar_pesoNegativo_deveLancarInvalidDataException() {
        Leitura l = new Leitura(null, contenedorFixture(), new Date(), 50.0, -5.0);

        assertThatThrownBy(() -> leituraService.salvar(l))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Peso em kg não pode ser negativo");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("atualizar deve modificar campos e retornar leitura atualizada")
    void atualizar_dadosValidos_deveRetornarLeituraAtualizada() {
        Contenedor c = contenedorFixture();
        Leitura existente  = new Leitura(1L, c, new Date(), 50.0, 80.0);
        Leitura param      = new Leitura(null, null, null, 95.0, 250.0);
        Leitura atualizada = new Leitura(1L, c, new Date(), 95.0, 250.0);
        when(leituraRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(leituraRepository.save(any(Leitura.class))).thenReturn(atualizada);

        Leitura resultado = leituraService.atualizar(1L, param);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNivelPercent()).isEqualTo(95.0);
        assertThat(resultado.getPesoKg()).isEqualTo(250.0);
        verify(leituraRepository).findById(1L);
        verify(leituraRepository).save(any(Leitura.class));
    }

    @Test
    @DisplayName("atualizar com ID inexistente deve lançar ResourceNotFoundException")
    void atualizar_idInexistente_deveLancarResourceNotFoundException() {
        when(leituraRepository.findById(999L)).thenReturn(Optional.empty());
        Leitura param = new Leitura(null, null, null, 50.0, 100.0);

        assertThatThrownBy(() -> leituraService.atualizar(999L, param))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("atualizar com ID inválido deve lançar InvalidDataException")
    void atualizar_idInvalido_deveLancarInvalidDataException() {
        Leitura param = new Leitura(null, null, null, 50.0, 100.0);

        assertThatThrownBy(() -> leituraService.atualizar(0L, param))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("atualizar com dados nulos deve lançar InvalidDataException")
    void atualizar_dadosNulos_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> leituraService.atualizar(1L, null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Dados da leitura não podem ser nulos");

        verifyNoInteractions(leituraRepository);
    }

    @Test
    @DisplayName("excluir deve remover leitura quando ID existente")
    void excluir_idExistente_deveRemoverLeitura() {
        when(leituraRepository.existsById(1L)).thenReturn(true);
        doNothing().when(leituraRepository).deleteById(1L);

        leituraService.excluir(1L);

        verify(leituraRepository).existsById(1L);
        verify(leituraRepository).deleteById(1L);
    }

    @Test
    @DisplayName("excluir com ID inexistente deve lançar ResourceNotFoundException")
    void excluir_idInexistente_deveLancarResourceNotFoundException() {
        when(leituraRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> leituraService.excluir(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(leituraRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("excluir com ID inválido deve lançar InvalidDataException")
    void excluir_idInvalido_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> leituraService.excluir(-1L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(leituraRepository);
    }
}
