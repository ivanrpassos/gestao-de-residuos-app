package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.model.Leitura;
import br.com.fiap.gestao_residuos.service.LeituraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LeituraController {

    @Autowired
    private LeituraService leituraService;

    @GetMapping("/leituras")
    public ResponseEntity<List<Leitura>> listarTodos() {
        List<Leitura> leituras = leituraService.listarTodos();
        return ResponseEntity.ok(leituras);
    }

    @GetMapping("/leitura/{id}")
    public ResponseEntity<Leitura> buscarPorId(@PathVariable Long id) {
        return leituraService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/leitura")
    public ResponseEntity<Leitura> salvar(@RequestBody Leitura leitura) {
        Leitura novo = leituraService.salvar(leitura);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @PutMapping("/leitura/{id}")
    public ResponseEntity<Leitura> atualizar(@PathVariable Long id, @RequestBody Leitura leitura) {
        Leitura atualizado = leituraService.atualizar(id, leitura);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/leitura/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        leituraService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
