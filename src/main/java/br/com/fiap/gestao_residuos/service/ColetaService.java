package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
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
        try {
            return coletaRepository.findAll();
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao listar coletas: " + ex.getMessage(), ex);
        }
    }

    public Optional<Coleta> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        return coletaRepository.findById(id);
    }

    public Coleta salvar(Coleta coleta) {
        if (coleta == null) {
            throw new InvalidDataException("Coleta não pode ser nula");
        }
        if (coleta.getDataAgendada() == null) {
            throw new InvalidDataException("Data agendada é obrigatória");
        }
        if (coleta.getStatus() == null || coleta.getStatus().isBlank()) {
            throw new InvalidDataException("Status é obrigatório");
        }
        try {
            return coletaRepository.save(coleta);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao salvar coleta: " + ex.getMessage(), ex);
        }
    }

    public Coleta atualizar(Long id, Coleta coletaParam) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (coletaParam == null) {
            throw new InvalidDataException("Dados da coleta não podem ser nulos");
        }

        return coletaRepository.findById(id)
                .map(coleta -> {
                    if (coletaParam.getRota() != null) {
                        coleta.setRota(coletaParam.getRota());
                    }
                    if (coletaParam.getStatus() != null && !coletaParam.getStatus().isBlank()) {
                        coleta.setStatus(coletaParam.getStatus());
                    }
                    if (coletaParam.getContenedor() != null) {
                        coleta.setContenedor(coletaParam.getContenedor());
                    }
                    if (coletaParam.getDataAgendada() != null) {
                        coleta.setDataAgendada(coletaParam.getDataAgendada());
                    }
                    try {
                        return coletaRepository.save(coleta);
                    } catch (Exception ex) {
                        throw new RuntimeException("Erro ao atualizar coleta: " + ex.getMessage(), ex);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Coleta não encontrada com ID: " + id));
    }

    public void excluir(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (!coletaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coleta não encontrada com ID: " + id);
        }
        try {
            coletaRepository.deleteById(id);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao excluir coleta: " + ex.getMessage(), ex);
        }
    }
}
