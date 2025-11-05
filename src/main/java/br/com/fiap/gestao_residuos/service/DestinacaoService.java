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

    public Destinacao salvar(Destinacao destinacao) {
        return destinacaoRepository.save(destinacao);
    }

    public Destinacao atualizar(Long id, Destinacao destinacaoAtualizado) {
        return destinacaoRepository.findById(id)
                .map(destinacao -> {
                    destinacao.setQuantidadeKg(destinacaoAtualizado.getQuantidadeKg());
                    destinacao.setTipoMaterial(destinacaoAtualizado.getTipoMaterial());
                    destinacao.setDataRegistro(destinacaoAtualizado.getDataRegistro());
                    destinacao.setLocalDestino(destinacaoAtualizado.getLocalDestino());

                    return destinacaoRepository.save(destinacao);
                })
                .orElseThrow(() -> new RuntimeException("Contêiner não encontrado com ID: " + id));
    }

    public void excluir(Long id) {
        destinacaoRepository.deleteById(id);
    }
}
