package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.model.Leitura;
import br.com.fiap.gestao_residuos.repository.LeituraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeituraService {

    @Autowired
    private LeituraRepository leituraRepository;

    public List<Leitura> listarTodos() {
        return leituraRepository.findAll();
    }

    public Optional<Leitura> buscarPorId(Long id) {
        return leituraRepository.findById(id);
    }

    public Leitura salvar(Leitura leitura) {
        return leituraRepository.save(leitura);
    }

    public Leitura atualizar(Long id, Leitura leituraParam) {
        return leituraRepository.findById(id)
                .map(leitura -> {
                    leitura.setPesoKg(leituraParam.getPesoKg());
                    leitura.setDataHora(leituraParam.getDataHora());
                    leitura.setContenedor(leituraParam.getContenedor());
                    leitura.setNivelPercent(leituraParam.getNivelPercent());

                    return leituraRepository.save(leitura);
                })
                .orElseThrow(() -> new RuntimeException("Leitura não encontrada com ID: " + id));
    }

    public void excluir(Long id) {
        leituraRepository.deleteById(id);
    }
}
