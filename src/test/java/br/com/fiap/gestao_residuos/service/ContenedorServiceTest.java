package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Contenedor;
import br.com.fiap.gestao_residuos.repository.ContenedorRepository;
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
@DisplayName("ContenedorService - Testes Unitários")
public class ContenedorServiceTest {

    @Mock
    private ContenedorRepository contenedorRepository;

    @InjectMocks
    private ContenedorService contenedorService;

    @Test
    @DisplayName("listarTodos deve retornar lista de contenedores")
    void listarTodos_deveRetornarListaDeContenedores() {
        Contenedor c1 = new Contenedor(1L, "Av. Paulista, 100", 500.0, "PLASTICO");
        Contenedor c2 = new Contenedor(2L, "Rua Augusta, 50",   300.0, "VIDRO");
        when(contenedorRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Contenedor> resultado = contenedorService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getTipoMaterial()).isEqualTo("PLASTICO");
        assertThat(resultado.get(1).getTipoMaterial()).isEqualTo("VIDRO");
        verify(contenedorRepository).findAll();
    }

    @Test
    @DisplayName("listarTodos deve retornar lista vazia quando não há registros")
    void listarTodos_semRegistros_deveRetornarListaVazia() {
        when(contenedorRepository.findAll()).thenReturn(List.of());

        List<Contenedor> resultado = contenedorService.listarTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId deve retornar Optional com contenedor quando ID válido")
    void buscarPorId_idValido_deveRetornarContenedor() {
        Contenedor c = new Contenedor(1L, "Av. Paulista, 100", 500.0, "PLASTICO");
        when(contenedorRepository.findById(1L)).thenReturn(Optional.of(c));

        Optional<Contenedor> resultado = contenedorService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getLocalizacao()).isEqualTo("Av. Paulista, 100");
        verify(contenedorRepository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId deve retornar Optional vazio para ID inexistente")
    void buscarPorId_idInexistente_deveRetornarOptionalVazio() {
        when(contenedorRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Contenedor> resultado = contenedorService.buscarPorId(999L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId com ID nulo deve lançar InvalidDataException")
    void buscarPorId_idNulo_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> contenedorService.buscarPorId(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(contenedorRepository);
    }

    @Test
    @DisplayName("buscarPorId com ID negativo deve lançar InvalidDataException")
    void buscarPorId_idNegativo_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> contenedorService.buscarPorId(-1L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(contenedorRepository);
    }

    @Test
    @DisplayName("salvar deve persistir e retornar contenedor com dados válidos")
    void salvar_dadosValidos_deveRetornarContenedorSalvo() {
        Contenedor entrada = new Contenedor(null, "Rua Nova, 10", 200.0, "METAL");
        Contenedor salvo   = new Contenedor(1L,   "Rua Nova, 10", 200.0, "METAL");
        when(contenedorRepository.save(any(Contenedor.class))).thenReturn(salvo);

        Contenedor resultado = contenedorService.salvar(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getLocalizacao()).isEqualTo("Rua Nova, 10");
        assertThat(resultado.getTipoMaterial()).isEqualTo("METAL");
        verify(contenedorRepository).save(any(Contenedor.class));
    }

    @Test
    @DisplayName("salvar com contenedor nulo deve lançar InvalidDataException")
    void salvar_contenedorNulo_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> contenedorService.salvar(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Contêiner não pode ser nulo");

        verifyNoInteractions(contenedorRepository);
    }

    @Test
    @DisplayName("salvar sem localização deve lançar InvalidDataException")
    void salvar_semLocalizacao_deveLancarInvalidDataException() {
        Contenedor c = new Contenedor(null, null, 500.0, "PLASTICO");

        assertThatThrownBy(() -> contenedorService.salvar(c))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Localização é obrigatória");

        verifyNoInteractions(contenedorRepository);
    }

    @Test
    @DisplayName("salvar sem tipo de material deve lançar InvalidDataException")
    void salvar_semTipoMaterial_deveLancarInvalidDataException() {
        Contenedor c = new Contenedor(null, "Av. X, 1", 500.0, null);

        assertThatThrownBy(() -> contenedorService.salvar(c))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Tipo de material é obrigatório");

        verifyNoInteractions(contenedorRepository);
    }

    @Test
    @DisplayName("salvar com capacidade zero deve lançar InvalidDataException")
    void salvar_capacidadeZero_deveLancarInvalidDataException() {
        Contenedor c = new Contenedor(null, "Av. X, 1", 0.0, "PLASTICO");

        assertThatThrownBy(() -> contenedorService.salvar(c))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Capacidade em litros deve ser maior que zero");

        verifyNoInteractions(contenedorRepository);
    }

    @Test
    @DisplayName("salvar com capacidade negativa deve lançar InvalidDataException")
    void salvar_capacidadeNegativa_deveLancarInvalidDataException() {
        Contenedor c = new Contenedor(null, "Av. X, 1", -50.0, "PLASTICO");

        assertThatThrownBy(() -> contenedorService.salvar(c))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Capacidade em litros deve ser maior que zero");

        verifyNoInteractions(contenedorRepository);
    }

    @Test
    @DisplayName("atualizar deve modificar campos e retornar contenedor atualizado")
    void atualizar_dadosValidos_deveRetornarContenedorAtualizado() {
        Contenedor existente  = new Contenedor(1L, "Av. Velha, 1", 300.0, "PLASTICO");
        Contenedor param      = new Contenedor(null, "Av. Nova, 99", 600.0, "ORGANICO");
        Contenedor atualizado = new Contenedor(1L, "Av. Nova, 99", 600.0, "ORGANICO");
        when(contenedorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(contenedorRepository.save(any(Contenedor.class))).thenReturn(atualizado);

        Contenedor resultado = contenedorService.atualizar(1L, param);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getLocalizacao()).isEqualTo("Av. Nova, 99");
        assertThat(resultado.getTipoMaterial()).isEqualTo("ORGANICO");
        verify(contenedorRepository).findById(1L);
        verify(contenedorRepository).save(any(Contenedor.class));
    }

    @Test
    @DisplayName("atualizar com ID inexistente deve lançar ResourceNotFoundException")
    void atualizar_idInexistente_deveLancarResourceNotFoundException() {
        when(contenedorRepository.findById(999L)).thenReturn(Optional.empty());
        Contenedor param = new Contenedor(null, "Av. X", 100.0, "METAL");

        assertThatThrownBy(() -> contenedorService.atualizar(999L, param))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("atualizar com ID inválido deve lançar InvalidDataException")
    void atualizar_idInvalido_deveLancarInvalidDataException() {
        Contenedor param = new Contenedor(null, "Av. X", 100.0, "METAL");

        assertThatThrownBy(() -> contenedorService.atualizar(-1L, param))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(contenedorRepository);
    }

    @Test
    @DisplayName("excluir deve remover contenedor quando ID existente")
    void excluir_idExistente_deveRemoverContenedor() {
        when(contenedorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(contenedorRepository).deleteById(1L);

        contenedorService.excluir(1L);

        verify(contenedorRepository).existsById(1L);
        verify(contenedorRepository).deleteById(1L);
    }

    @Test
    @DisplayName("excluir com ID inexistente deve lançar ResourceNotFoundException")
    void excluir_idInexistente_deveLancarResourceNotFoundException() {
        when(contenedorRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> contenedorService.excluir(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(contenedorRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("excluir com ID zero deve lançar InvalidDataException")
    void excluir_idZero_deveLancarInvalidDataException() {
        assertThatThrownBy(() -> contenedorService.excluir(0L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID inválido");

        verifyNoInteractions(contenedorRepository);
    }
}
