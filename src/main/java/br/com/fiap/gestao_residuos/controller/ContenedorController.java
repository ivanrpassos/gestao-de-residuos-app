package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.model.Contenedor;
import br.com.fiap.gestao_residuos.service.ContenedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping
public class ContenedorController {

    @Autowired
    private ContenedorService contenedorService;

    @GetMapping("/contenedores")
    public ResponseEntity<List<Contenedor>> listarTodos() {
        return ResponseEntity.ok(contenedorService.listarTodos());
    }

    @GetMapping("/contenedor/{id}")
    public ResponseEntity<Contenedor> buscarPorId(@PathVariable Long id) {
        return contenedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/contenedor")
    public ResponseEntity<Contenedor> salvar(@RequestBody Contenedor contenedor) {
        Contenedor novo = contenedorService.salvar(contenedor);
        return ResponseEntity.ok(novo);
    }

    @PutMapping("/contenedor/{id}")
    public ResponseEntity<Contenedor> atualizar(@PathVariable Long id, @RequestBody Contenedor contenedor) {
        Contenedor atualizado = contenedorService.atualizar(id, contenedor);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/contenedor/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        contenedorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
