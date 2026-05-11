package br.com.fiap.gestao_residuos.api;

import br.com.fiap.gestao_residuos.config.JacksonConfig;
import br.com.fiap.gestao_residuos.controller.ColetaController;
import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Coleta;
import br.com.fiap.gestao_residuos.service.ColetaService;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(ColetaController.class)
@Import(JacksonConfig.class)
@DisplayName("Testes de API - Coleta")
class ColetaApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ColetaService coletaService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @DisplayName("GET /api/coletas deve retornar 200 com lista de coletas")
    void listarTodos_deveRetornar200() {
        List<Coleta> lista = List.of(
                new Coleta(1L, null, null, new Date(), "AGENDADO"),
                new Coleta(2L, null, null, new Date(), "CONCLUIDO")
        );
        when(coletaService.listarTodos()).thenReturn(lista);

        RestAssuredMockMvc.given()
                .when().get("/api/coletas")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2))
                .body("[0].status", equalTo("AGENDADO"))
                .body("[1].status", equalTo("CONCLUIDO"));
    }

    @Test
    @DisplayName("GET /api/coleta/{id} deve retornar 200 e validar JSON Schema")
    void buscarPorId_existente_deveRetornar200ComSchema() {
        Coleta c = new Coleta(1L, null, null, new Date(), "AGENDADO");
        when(coletaService.buscarPorId(1L)).thenReturn(Optional.of(c));

        RestAssuredMockMvc.given()
                .when().get("/api/coleta/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/coleta-schema.json"))
                .body("id", equalTo(1))
                .body("status", equalTo("AGENDADO"));
    }

    @Test
    @DisplayName("GET /api/coleta/{id} deve retornar 404 para ID inexistente")
    void buscarPorId_inexistente_deveRetornar404() {
        when(coletaService.buscarPorId(9999L)).thenReturn(Optional.empty());

        RestAssuredMockMvc.given()
                .when().get("/api/coleta/9999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("POST /api/coleta deve criar coleta e retornar 201")
    void salvar_dadosValidos_deveRetornar201() {
        Coleta salva = new Coleta(1L, null, null, new Date(), "AGENDADO");
        when(coletaService.salvar(any(Coleta.class))).thenReturn(salva);

        String body = """
                {
                  "dataAgendada": 1700000000000,
                  "status": "AGENDADO"
                }""";

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(body)
                .when().post("/api/coleta")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/coleta-schema.json"))
                .body("id", notNullValue())
                .body("status", equalTo("AGENDADO"));
    }

    @Test
    @DisplayName("POST /api/coleta sem status deve retornar 400")
    void salvar_semStatus_deveRetornar400() {
        when(coletaService.salvar(any(Coleta.class)))
                .thenThrow(new InvalidDataException("Status é obrigatório"));

        String body = """
                {
                  "dataAgendada": 1700000000000
                }""";

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(body)
                .when().post("/api/coleta")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("Dados inválidos"))
                .body("message", containsString("Status é obrigatório"))
                .body("status", equalTo(400));
    }

    @Test
    @DisplayName("POST /api/coleta sem data agendada deve retornar 400")
    void salvar_semDataAgendada_deveRetornar400() {
        when(coletaService.salvar(any(Coleta.class)))
                .thenThrow(new InvalidDataException("Data agendada é obrigatória"));

        String body = """
                {
                  "status": "AGENDADO"
                }""";

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(body)
                .when().post("/api/coleta")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("Dados inválidos"))
                .body("status", equalTo(400));
    }

    @Test
    @DisplayName("PUT /api/coleta/{id} deve atualizar e retornar 200")
    void atualizar_existente_deveRetornar200() {
        Coleta atualizada = new Coleta(1L, null, null, new Date(), "CONCLUIDO");
        when(coletaService.atualizar(eq(1L), any(Coleta.class))).thenReturn(atualizada);

        String body = """
                {
                  "status": "CONCLUIDO"
                }""";

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(body)
                .when().put("/api/coleta/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("CONCLUIDO"));
    }

    @Test
    @DisplayName("PUT /api/coleta/{id} deve retornar 404 para ID inexistente")
    void atualizar_inexistente_deveRetornar404() {
        when(coletaService.atualizar(anyLong(), any(Coleta.class)))
                .thenThrow(new ResourceNotFoundException("Coleta não encontrada com ID: 9999"));

        String body = """
                {
                  "status": "CONCLUIDO"
                }""";

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(body)
                .when().put("/api/coleta/9999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("Recurso não encontrado"));
    }

    @Test
    @DisplayName("DELETE /api/coleta/{id} deve excluir e retornar 204")
    void excluir_existente_deveRetornar204() {
        RestAssuredMockMvc.given()
                .when().delete("/api/coleta/1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("DELETE /api/coleta/{id} deve retornar 404 para ID inexistente")
    void excluir_inexistente_deveRetornar404() {
        doThrow(new ResourceNotFoundException("Coleta não encontrada com ID: 9999"))
                .when(coletaService).excluir(9999L);

        RestAssuredMockMvc.given()
                .when().delete("/api/coleta/9999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("Recurso não encontrado"))
                .body("status", equalTo(404));
    }
}
