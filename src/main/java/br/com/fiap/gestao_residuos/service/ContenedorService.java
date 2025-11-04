package br.com.fiap.gestao_residuos.service;

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
        return contenedorRepository.findAll();
    }

    public Optional<Contenedor> buscarPorId(Long id) {
        return contenedorRepository.findById(id);
    }

    public Contenedor salvar(Contenedor contenedor) {
        return contenedorRepository.save(contenedor);
    }

    public Contenedor atualizar(Long id, Contenedor contenedorAtualizado) {
        return contenedorRepository.findById(id)
                .map(contenedor -> {
                    contenedor.setLocalizacao(contenedorAtualizado.getLocalizacao());
                    contenedor.setTipoMaterial(contenedorAtualizado.getTipoMaterial());
                    contenedor.setCapacidadeLitros(contenedorAtualizado.getCapacidadeLitros());
                    return contenedorRepository.save(contenedor);
                })
                .orElseThrow(() -> new RuntimeException("Contêiner não encontrado com ID: " + id));
    }

    public void excluir(Long id) {
        contenedorRepository.deleteById(id);
    }
}
