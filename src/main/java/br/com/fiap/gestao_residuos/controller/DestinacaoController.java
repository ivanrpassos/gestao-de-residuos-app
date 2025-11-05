package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.model.Destinacao;
import br.com.fiap.gestao_residuos.service.DestinacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DestinacaoController {

    @Autowired
    private DestinacaoService destinacaoService;

    @GetMapping("/destinacoes")
    public ResponseEntity<List<Destinacao>> listarTodos() {
        List<Destinacao> destinacoes = destinacaoService.listarTodos();
        return ResponseEntity.ok(destinacoes);
    }

    @GetMapping("/destino/{id}")
    public ResponseEntity<Destinacao> buscarPorId(@PathVariable Long id) {
        return destinacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/destino")
    public ResponseEntity<Destinacao> salvar(@RequestBody Destinacao destino) {
        Destinacao novo = destinacaoService.salvar(destino);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @PutMapping("/destino/{id}")
    public ResponseEntity<Destinacao> atualizar(@PathVariable Long id, @RequestBody Destinacao destino) {
        Destinacao atualizado = destinacaoService.atualizar(id, destino);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/destino/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        destinacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
