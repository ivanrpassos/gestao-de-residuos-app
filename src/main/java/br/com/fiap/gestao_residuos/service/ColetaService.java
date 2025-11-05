package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.model.Coleta;
import br.com.fiap.gestao_residuos.repository.ColetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColetaService {

    @Autowired
    private ColetaRepository coletaRepository;


    public List<Coleta> listarTodos() {
        return coletaRepository.findAll();
    }

    public Optional<Coleta> buscarPorId(Long id) {
        return coletaRepository.findById(id);
    }

    public Coleta salvar(Coleta coleta) {
        return coletaRepository.save(coleta);
    }

    public Coleta atualizar(Long id, Coleta coletaParam) {
        return coletaRepository.findById(id)
                .map(coleta -> {
                    coleta.setRota(coletaParam.getRota());
                    coleta.setStatus(coletaParam.getStatus());
                    coleta.setContenedor(coletaParam.getContenedor());
                    coleta.setDataAgendada(coletaParam.getDataAgendada());

                    return coletaRepository.save(coleta);
                })
                .orElseThrow(() -> new RuntimeException("Coleta não encontrada com ID: " + id));
    }

    public void excluir(Long id) {
        coletaRepository.deleteById(id);
    }
}
