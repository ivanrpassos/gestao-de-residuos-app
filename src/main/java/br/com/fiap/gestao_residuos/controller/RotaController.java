package br.com.fiap.gestao_residuos.controller;

import br.com.fiap.gestao_residuos.model.Rota;
import br.com.fiap.gestao_residuos.service.RotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class RotaController {

    @Autowired
    private RotaService rotaService;


    @GetMapping("/rotas")
    public ResponseEntity<List<Rota>> listarTodos() {
        return ResponseEntity.ok(rotaService.listarTodos());
    }

    @GetMapping("/rota/{id}")
    public ResponseEntity<Rota> buscarPorId(@PathVariable Long id) {
        return rotaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rota")
    public ResponseEntity<Rota> salvar(@RequestBody Rota rota) {
        Rota novo = rotaService.salvar(rota);
        return ResponseEntity.ok(novo);
    }

    @PutMapping("/rota/{id}")
    public ResponseEntity<Rota> atualizar(@PathVariable Long id, @RequestBody Rota rota) {
        Rota atualizado = rotaService.atualizar(id, rota);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/rota/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        rotaService.excluir(id);
        return ResponseEntity.ok().build();
    }
}
