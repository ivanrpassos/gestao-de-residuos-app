package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.model.Rota;
import br.com.fiap.gestao_residuos.repository.RotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RotaService {

    @Autowired
    private RotaRepository rotaRepository;

    public List<Rota> listarTodos() {
        return rotaRepository.findAll();
    }

    public Optional<Rota> buscarPorId(Long id) {
        return rotaRepository.findById(id);
    }

    public Rota salvar(Rota rota) {
        return rotaRepository.save(rota);
    }

    public Rota atualizar(Long id, Rota rotaParam) {
        return rotaRepository.findById(id)
                .map(rota -> {
                    rota.setVeiculo(rotaParam.getVeiculo());
                    rota.setColetas(rotaParam.getColetas());
                    rota.setCapacidade(rotaParam.getCapacidade());
                    rota.setEnderecoBase(rotaParam.getEnderecoBase());

                    return rotaRepository.save(rota);
                })
                .orElseThrow(() -> new RuntimeException("Rota não encontrada com ID: " + id));
    }

    public void excluir(Long id) {
        rotaRepository.deleteById(id);
    }
}
