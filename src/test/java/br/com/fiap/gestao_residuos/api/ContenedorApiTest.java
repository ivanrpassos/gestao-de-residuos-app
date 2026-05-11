package br.com.fiap.gestao_residuos.api;

import br.com.fiap.gestao_residuos.config.JacksonConfig;
import br.com.fiap.gestao_residuos.controller.ContenedorController;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Contenedor;
import br.com.fiap.gestao_residuos.service.ContenedorService;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(ContenedorController.class)
@Import(JacksonConfig.class)
@DisplayName("Testes de API - Contenedor")
class ContenedorApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContenedorService contenedorService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @DisplayName("GET /api/contenedores deve retornar 200 com lista de contenedores")
    void listarTodos_deveRetornar200() {
        List<Contenedor> lista = List.of(
                new Contenedor(1L, "Av. Paulista, 1000", 500.0, "PLASTICO"),
                new Contenedor(2L, "Rua Augusta, 200", 300.0, "VIDRO")
        );
        when(contenedorService.listarTodos()).thenReturn(lista);

        RestAssuredMockMvc.given()
                .when().get("/api/contenedores")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2))
                .body("[0].id", equalTo(1))
                .body("[0].localizacao", equalTo("Av. Paulista, 1000"))
                .body("[0].tipoMaterial", equalTo("PLASTICO"));
    }

    @Test
    @DisplayName("GET /api/contenedores deve retornar lista vazia com status 200")
    void listarTodos_listaVazia_deveRetornar200() {
        when(contenedorService.listarTodos()).thenReturn(List.of());

        RestAssuredMockMvc.given()
                .when().get("/api/contenedores")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /api/contenedor/{id} deve retornar 200 e validar JSON Schema")
    void buscarPorId_existente_deveRetornar200ComSchema() {
        Contenedor c = new Contenedor(1L, "Av. Paulista, 1000", 500.0, "PLASTICO");
        when(contenedorService.buscarPorId(1L)).thenReturn(Optional.of(c));

        RestAssuredMockMvc.given()
                .when().get("/api/contenedor/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/contenedor-schema.json"))
                .body("id", equalTo(1))
                .body("localizacao", equalTo("Av. Paulista, 1000"))
                .body("capacidadeLitros", equalTo(500.0f))
                .body("tipoMaterial", equalTo("PLASTICO"));
    }

    @Test
    @DisplayName("GET /api/contenedor/{id} deve retornar 404 para ID inexistente")
    void buscarPorId_inexistente_deveRetornar404() {
        when(contenedorService.buscarPorId(9999L)).thenReturn(Optional.empty());

        RestAssuredMockMvc.given()
                .when().get("/api/contenedor/9999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("POST /api/contenedor deve criar contenedor e retornar 201")
    void salvar_dadosValidos_deveRetornar201() {
        Contenedor salvo = new Contenedor(1L, "Rua das Flores, 100", 600.0, "PAPEL");
        when(contenedorService.salvar(any(Contenedor.class))).thenReturn(salvo);

        String body = """
                {
                  "localizacao": "Rua das Flores, 100",
                  "capacidadeLitros": 600.0,
                  "tipoMaterial": "PAPEL"
                }""";

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(body)
                .when().post("/api/contenedor")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/contenedor-schema.json"))
                .body("id", notNullValue())
                .body("localizacao", equalTo("Rua das Flores, 100"))
                .body("tipoMaterial", equalTo("PAPEL"));
    }

    @Test
    @DisplayName("PUT /api/contenedor/{id} deve atualizar e retornar 200")
    void atualizar_existente_deveRetornar200() {
        Contenedor atualizado = new Contenedor(1L, "Rua Nova, 999", 700.0, "METAL");
        when(contenedorService.atualizar(eq(1L), any(Contenedor.class))).thenReturn(atualizado);

        String body = """
                {
                  "localizacao": "Rua Nova, 999",
                  "capacidadeLitros": 700.0,
                  "tipoMaterial": "METAL"
                }""";

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(body)
                .when().put("/api/contenedor/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("localizacao", equalTo("Rua Nova, 999"))
                .body("tipoMaterial", equalTo("METAL"));
    }

    @Test
    @DisplayName("PUT /api/contenedor/{id} deve retornar 404 para ID inexistente")
    void atualizar_inexistente_deveRetornar404() {
        when(contenedorService.atualizar(anyLong(), any(Contenedor.class)))
                .thenThrow(new ResourceNotFoundException("Contenedor não encontrado com ID: 9999"));

        String body = """
                {
                  "localizacao": "Rua X",
                  "capacidadeLitros": 100.0,
                  "tipoMaterial": "PLASTICO"
                }""";

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(body)
                .when().put("/api/contenedor/9999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("Recurso não encontrado"));
    }

    @Test
    @DisplayName("DELETE /api/contenedor/{id} deve excluir e retornar 204")
    void excluir_existente_deveRetornar204() {
        RestAssuredMockMvc.given()
                .when().delete("/api/contenedor/1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("DELETE /api/contenedor/{id} deve retornar 404 para ID inexistente")
    void excluir_inexistente_deveRetornar404() {
        doThrow(new ResourceNotFoundException("Contenedor não encontrado com ID: 9999"))
                .when(contenedorService).excluir(9999L);

        RestAssuredMockMvc.given()
                .when().delete("/api/contenedor/9999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("Recurso não encontrado"))
                .body("status", equalTo(404));
    }
}
