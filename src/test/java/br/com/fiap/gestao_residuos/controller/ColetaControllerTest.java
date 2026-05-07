package br.com.fiap.gestao_residuos.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import br.com.fiap.gestao_residuos.model.Coleta;

@SpringBootTest
public class ColetaControllerTest {
    @Autowired
    private ColetaController coletaController;

    @Test
    public void testListarTodos() {
        ResponseEntity<List<Coleta>> coletas = coletaController.listarTodos();
        assertNotNull(coletas);
        assertTrue(coletas.getBody().size() > 0);
    }
}
