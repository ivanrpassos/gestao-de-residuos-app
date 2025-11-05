package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
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
        try {
            return rotaRepository.findAll();
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao listar rotas: " + ex.getMessage(), ex);
        }
    }

    public Optional<Rota> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        return rotaRepository.findById(id);
    }

    public Rota salvar(Rota rota) {
        if (rota == null) {
            throw new InvalidDataException("Rota não pode ser nula");
        }
        if (rota.getVeiculo() == null || rota.getVeiculo().isBlank()) {
            throw new InvalidDataException("Veículo é obrigatório");
        }
        if (rota.getEnderecoBase() == null || rota.getEnderecoBase().isBlank()) {
            throw new InvalidDataException("Endereço base é obrigatório");
        }
        if (rota.getCapacidade() == null || rota.getCapacidade() <= 0) {
            throw new InvalidDataException("Capacidade deve ser maior que zero");
        }
        try {
            return rotaRepository.save(rota);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao salvar rota: " + ex.getMessage(), ex);
        }
    }

    public Rota atualizar(Long id, Rota rotaParam) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (rotaParam == null) {
            throw new InvalidDataException("Dados da rota não podem ser nulos");
        }

        return rotaRepository.findById(id)
                .map(rota -> {
                    if (rotaParam.getVeiculo() != null && !rotaParam.getVeiculo().isBlank()) {
                        rota.setVeiculo(rotaParam.getVeiculo());
                    }
                    if (rotaParam.getEnderecoBase() != null && !rotaParam.getEnderecoBase().isBlank()) {
                        rota.setEnderecoBase(rotaParam.getEnderecoBase());
                    }
                    if (rotaParam.getCapacidade() != null && rotaParam.getCapacidade() > 0) {
                        rota.setCapacidade(rotaParam.getCapacidade());
                    }
                    if (rotaParam.getColetas() != null) {
                        rota.setColetas(rotaParam.getColetas());
                    }
                    try {
                        return rotaRepository.save(rota);
                    } catch (Exception ex) {
                        throw new RuntimeException("Erro ao atualizar rota: " + ex.getMessage(), ex);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Rota não encontrada com ID: " + id));
    }

    public void excluir(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (!rotaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rota não encontrada com ID: " + id);
        }
        try {
            rotaRepository.deleteById(id);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao excluir rota: " + ex.getMessage(), ex);
        }
    }
}
