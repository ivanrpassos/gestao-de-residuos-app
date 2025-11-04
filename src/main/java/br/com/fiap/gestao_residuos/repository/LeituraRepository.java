package br.com.fiap.gestao_residuos.repository;

import br.com.fiap.gestao_residuos.model.Leitura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeituraRepository extends JpaRepository<Leitura, Long> {
}
