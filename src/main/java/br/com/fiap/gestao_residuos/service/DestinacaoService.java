package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
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
        try {
            return destinacaoRepository.findAll();
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao listar destinações: " + ex.getMessage(), ex);
        }
    }

    public Optional<Destinacao> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        return destinacaoRepository.findById(id);
    }

    public Destinacao salvar(Destinacao destinacao) {
        if (destinacao == null) {
            throw new InvalidDataException("Destinação não pode ser nula");
        }
        if (destinacao.getTipoMaterial() == null || destinacao.getTipoMaterial().isBlank()) {
            throw new InvalidDataException("Tipo de material é obrigatório");
        }
        if (destinacao.getLocalDestino() == null || destinacao.getLocalDestino().isBlank()) {
            throw new InvalidDataException("Local de destino é obrigatório");
        }
        if (destinacao.getQuantidadeKg() == null || destinacao.getQuantidadeKg() <= 0) {
            throw new InvalidDataException("Quantidade em kg deve ser maior que zero");
        }
        if (destinacao.getDataRegistro() == null) {
            throw new InvalidDataException("Data de registro é obrigatória");
        }
        try {
            return destinacaoRepository.save(destinacao);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao salvar destinação: " + ex.getMessage(), ex);
        }
    }

    public Destinacao atualizar(Long id, Destinacao destinacaoAtualizado) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (destinacaoAtualizado == null) {
            throw new InvalidDataException("Dados da destinação não podem ser nulos");
        }

        return destinacaoRepository.findById(id)
                .map(destinacao -> {
                    if (destinacaoAtualizado.getTipoMaterial() != null && !destinacaoAtualizado.getTipoMaterial().isBlank()) {
                        destinacao.setTipoMaterial(destinacaoAtualizado.getTipoMaterial());
                    }
                    if (destinacaoAtualizado.getLocalDestino() != null && !destinacaoAtualizado.getLocalDestino().isBlank()) {
                        destinacao.setLocalDestino(destinacaoAtualizado.getLocalDestino());
                    }
                    if (destinacaoAtualizado.getQuantidadeKg() != null && destinacaoAtualizado.getQuantidadeKg() > 0) {
                        destinacao.setQuantidadeKg(destinacaoAtualizado.getQuantidadeKg());
                    }
                    if (destinacaoAtualizado.getDataRegistro() != null) {
                        destinacao.setDataRegistro(destinacaoAtualizado.getDataRegistro());
                    }
                    try {
                        return destinacaoRepository.save(destinacao);
                    } catch (Exception ex) {
                        throw new RuntimeException("Erro ao atualizar destinação: " + ex.getMessage(), ex);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Destinação não encontrada com ID: " + id));
    }

    public void excluir(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (!destinacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Destinação não encontrada com ID: " + id);
        }
        try {
            destinacaoRepository.deleteById(id);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao excluir destinação: " + ex.getMessage(), ex);
        }
    }
}
