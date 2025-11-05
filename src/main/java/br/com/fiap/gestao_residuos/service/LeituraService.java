package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
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
        try {
            return leituraRepository.findAll();
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao listar leituras: " + ex.getMessage(), ex);
        }
    }

    public Optional<Leitura> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        return leituraRepository.findById(id);
    }

    public Leitura salvar(Leitura leitura) {
        if (leitura == null) {
            throw new InvalidDataException("Leitura não pode ser nula");
        }
        if (leitura.getContenedor() == null) {
            throw new InvalidDataException("Contêiner é obrigatório para a leitura");
        }
        if (leitura.getDataHora() == null) {
            throw new InvalidDataException("Data e hora são obrigatórias");
        }
        if (leitura.getNivelPercent() == null || leitura.getNivelPercent() < 0 || leitura.getNivelPercent() > 100) {
            throw new InvalidDataException("Nível percentual deve estar entre 0 e 100");
        }
        if (leitura.getPesoKg() == null || leitura.getPesoKg() < 0) {
            throw new InvalidDataException("Peso em kg não pode ser negativo");
        }
        try {
            return leituraRepository.save(leitura);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao salvar leitura: " + ex.getMessage(), ex);
        }
    }

    public Leitura atualizar(Long id, Leitura leituraParam) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (leituraParam == null) {
            throw new InvalidDataException("Dados da leitura não podem ser nulos");
        }

        return leituraRepository.findById(id)
                .map(leitura -> {
                    if (leituraParam.getContenedor() != null) {
                        leitura.setContenedor(leituraParam.getContenedor());
                    }
                    if (leituraParam.getDataHora() != null) {
                        leitura.setDataHora(leituraParam.getDataHora());
                    }
                    if (leituraParam.getNivelPercent() != null && leituraParam.getNivelPercent() >= 0 && leituraParam.getNivelPercent() <= 100) {
                        leitura.setNivelPercent(leituraParam.getNivelPercent());
                    }
                    if (leituraParam.getPesoKg() != null && leituraParam.getPesoKg() >= 0) {
                        leitura.setPesoKg(leituraParam.getPesoKg());
                    }
                    try {
                        return leituraRepository.save(leitura);
                    } catch (Exception ex) {
                        throw new RuntimeException("Erro ao atualizar leitura: " + ex.getMessage(), ex);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Leitura não encontrada com ID: " + id));
    }

    public void excluir(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (!leituraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leitura não encontrada com ID: " + id);
        }
        try {
            leituraRepository.deleteById(id);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao excluir leitura: " + ex.getMessage(), ex);
        }
    }
}
