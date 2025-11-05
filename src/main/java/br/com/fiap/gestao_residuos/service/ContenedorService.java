package br.com.fiap.gestao_residuos.service;

import br.com.fiap.gestao_residuos.exception.InvalidDataException;
import br.com.fiap.gestao_residuos.exception.ResourceNotFoundException;
import br.com.fiap.gestao_residuos.model.Contenedor;
import br.com.fiap.gestao_residuos.repository.ContenedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContenedorService {

    @Autowired
    private ContenedorRepository contenedorRepository;

    public List<Contenedor> listarTodos() {
        try {
            return contenedorRepository.findAll();
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao listar contêineres: " + ex.getMessage(), ex);
        }
    }

    public Optional<Contenedor> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        return contenedorRepository.findById(id);
    }

    public Contenedor salvar(Contenedor contenedor) {
        if (contenedor == null) {
            throw new InvalidDataException("Contêiner não pode ser nulo");
        }
        if (contenedor.getLocalizacao() == null || contenedor.getLocalizacao().isBlank()) {
            throw new InvalidDataException("Localização é obrigatória");
        }
        if (contenedor.getTipoMaterial() == null || contenedor.getTipoMaterial().isBlank()) {
            throw new InvalidDataException("Tipo de material é obrigatório");
        }
        if (contenedor.getCapacidadeLitros() == null || contenedor.getCapacidadeLitros() <= 0) {
            throw new InvalidDataException("Capacidade em litros deve ser maior que zero");
        }
        try {
            return contenedorRepository.save(contenedor);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao salvar contêiner: " + ex.getMessage(), ex);
        }
    }

    public Contenedor atualizar(Long id, Contenedor contenedorAtualizado) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (contenedorAtualizado == null) {
            throw new InvalidDataException("Dados do contêiner não podem ser nulos");
        }

        return contenedorRepository.findById(id)
                .map(contenedor -> {
                    if (contenedorAtualizado.getLocalizacao() != null && !contenedorAtualizado.getLocalizacao().isBlank()) {
                        contenedor.setLocalizacao(contenedorAtualizado.getLocalizacao());
                    }
                    if (contenedorAtualizado.getTipoMaterial() != null && !contenedorAtualizado.getTipoMaterial().isBlank()) {
                        contenedor.setTipoMaterial(contenedorAtualizado.getTipoMaterial());
                    }
                    if (contenedorAtualizado.getCapacidadeLitros() != null && contenedorAtualizado.getCapacidadeLitros() > 0) {
                        contenedor.setCapacidadeLitros(contenedorAtualizado.getCapacidadeLitros());
                    }
                    try {
                        return contenedorRepository.save(contenedor);
                    } catch (Exception ex) {
                        throw new RuntimeException("Erro ao atualizar contêiner: " + ex.getMessage(), ex);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Contêiner não encontrado com ID: " + id));
    }

    public void excluir(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID inválido: deve ser um número positivo");
        }
        if (!contenedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contêiner não encontrado com ID: " + id);
        }
        try {
            contenedorRepository.deleteById(id);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao excluir contêiner: " + ex.getMessage(), ex);
        }
    }
}
