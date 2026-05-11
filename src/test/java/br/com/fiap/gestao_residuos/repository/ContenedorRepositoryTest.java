package br.com.fiap.gestao_residuos.repository;

import br.com.fiap.gestao_residuos.model.Contenedor;
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
@DisplayName("ContenedorRepository - Testes Unitários")
public class ContenedorRepositoryTest {

    @Mock
    private ContenedorRepository contenedorRepository;

    @Test
    @DisplayName("findAll deve retornar lista de contenedores")
    void findAll_deveRetornarListaDeContenedores() {
        Contenedor c1 = new Contenedor(1L, "Av. Paulista, 100", 500.0, "PLASTICO");
        Contenedor c2 = new Contenedor(2L, "Rua Augusta, 50",   300.0, "VIDRO");
        when(contenedorRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Contenedor> resultado = contenedorRepository.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getLocalizacao()).isEqualTo("Av. Paulista, 100");
        assertThat(resultado.get(0).getTipoMaterial()).isEqualTo("PLASTICO");
        assertThat(resultado.get(1).getTipoMaterial()).isEqualTo("VIDRO");
        verify(contenedorRepository).findAll();
    }

    @Test
    @DisplayName("findAll deve retornar lista vazia quando não há registros")
    void findAll_semRegistros_deveRetornarListaVazia() {
        when(contenedorRepository.findAll()).thenReturn(List.of());

        List<Contenedor> resultado = contenedorRepository.findAll();

        assertThat(resultado).isEmpty();
        verify(contenedorRepository).findAll();
    }

    @Test
    @DisplayName("findById deve retornar Optional com contenedor quando ID existente")
    void findById_idExistente_deveRetornarContenedor() {
        Contenedor c = new Contenedor(1L, "Av. Paulista, 100", 500.0, "PLASTICO");
        when(contenedorRepository.findById(1L)).thenReturn(Optional.of(c));

        Optional<Contenedor> resultado = contenedorRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getLocalizacao()).isEqualTo("Av. Paulista, 100");
        assertThat(resultado.get().getCapacidadeLitros()).isEqualTo(500.0);
        verify(contenedorRepository).findById(1L);
    }

    @Test
    @DisplayName("findById deve retornar Optional vazio quando ID não encontrado")
    void findById_idInexistente_deveRetornarOptionalVazio() {
        when(contenedorRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Contenedor> resultado = contenedorRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(contenedorRepository).findById(999L);
    }

    @Test
    @DisplayName("save deve persistir e retornar contenedor salvo com ID gerado")
    void save_deveRetornarContenedorSalvo() {
        Contenedor entrada = new Contenedor(null, "Rua Nova, 10", 200.0, "METAL");
        Contenedor salvo   = new Contenedor(1L,   "Rua Nova, 10", 200.0, "METAL");
        when(contenedorRepository.save(entrada)).thenReturn(salvo);

        Contenedor resultado = contenedorRepository.save(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getLocalizacao()).isEqualTo("Rua Nova, 10");
        assertThat(resultado.getTipoMaterial()).isEqualTo("METAL");
        verify(contenedorRepository).save(entrada);
    }

    @Test
    @DisplayName("existsById deve retornar true quando contenedor existe")
    void existsById_contenedorExistente_deveRetornarTrue() {
        when(contenedorRepository.existsById(1L)).thenReturn(true);

        boolean existe = contenedorRepository.existsById(1L);

        assertThat(existe).isTrue();
        verify(contenedorRepository).existsById(1L);
    }

    @Test
    @DisplayName("existsById deve retornar false quando contenedor não existe")
    void existsById_contenedorInexistente_deveRetornarFalse() {
        when(contenedorRepository.existsById(999L)).thenReturn(false);

        boolean existe = contenedorRepository.existsById(999L);

        assertThat(existe).isFalse();
        verify(contenedorRepository).existsById(999L);
    }

    @Test
    @DisplayName("deleteById deve executar exclusão sem lançar exceção")
    void deleteById_deveExcluirSemExcecao() {
        doNothing().when(contenedorRepository).deleteById(1L);

        contenedorRepository.deleteById(1L);

        verify(contenedorRepository).deleteById(1L);
    }
}
