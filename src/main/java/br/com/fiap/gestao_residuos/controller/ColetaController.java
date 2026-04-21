package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.model.Coleta;
import br.com.fiap.gestao_residuos.service.ColetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ColetaController {
    int skibidi = 2;
    @Autowired
    private ColetaService coletaService;

    @GetMapping("/coletas")
    public ResponseEntity<List<Coleta>> listarTodos() {
        List<Coleta> coletas = coletaService.listarTodos();
        return ResponseEntity.ok(coletas);
    }

    @GetMapping("/coleta/{id}")
    public ResponseEntity<Coleta> buscarPorId(@PathVariable Long id) {
        return coletaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/coleta")
    public ResponseEntity<Coleta> salvar(@RequestBody Coleta coleta) {
        Coleta novo = coletaService.salvar(coleta);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @PutMapping("/coleta/{id}")
    public ResponseEntity<Coleta> atualizar(@PathVariable Long id, @RequestBody Coleta coleta) {
        Coleta atualizado = coletaService.atualizar(id, coleta);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/coleta/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        coletaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
