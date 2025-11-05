package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.model.Destinacao;
import br.com.fiap.gestao_residuos.repository.DestinacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinacaoService {

    @Autowired
    private DestinacaoRepository destinacaoRepository;

    public List<Destinacao> listarTodos() {
        return destinacaoRepository.findAll();
    }

    public Optional<Destinacao> buscarPorId(Long id) {
        return destinacaoRepository.findById(id);
    }

    public Destinacao salvar(Destinacao contenedor) {
        return destinacaoRepository.save(contenedor);
    }

    public Destinacao atualizar(Long id, Destinacao contenedorAtualizado) {
        return destinacaoRepository.findById(id)
                .map(contenedor -> {
                    contenedor.setQuantidadeKg(contenedorAtualizado.getQuantidadeKg());
                    contenedor.setTipoMaterial(contenedorAtualizado.getTipoMaterial());
                    contenedor.setDataRegistro(contenedorAtualizado.getDataRegistro());
                    contenedor.setLocalDestino(contenedorAtualizado.getLocalDestino());

                    return destinacaoRepository.save(contenedor);
                })
                .orElseThrow(() -> new RuntimeException("Contêiner não encontrado com ID: " + id));
    }

    public void excluir(Long id) {
        destinacaoRepository.deleteById(id);
    }
}
