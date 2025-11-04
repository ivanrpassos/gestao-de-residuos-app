package br.com.fiap.gestao_residuos.repository;

import br.com.fiap.gestao_residuos.model.Destinacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DestinacaoRepository extends JpaRepository<Destinacao, Long> {
}
