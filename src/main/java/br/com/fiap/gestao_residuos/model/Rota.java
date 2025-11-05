package br.com.fiap.gestao_residuos.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "ROTA")
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ROTA")
    private Long id;

    @Column(name = "VEICULO", length = 100)
    private String veiculo;

    @Column(name = "ENDERECO_BASE", length = 100)
    private String enderecoBase;

    @Column(name = "CAPACIDADE")
    private Double capacidade;

    @OneToMany(mappedBy = "rota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Coleta> coletas;

    public Rota() { }

    public Rota(Long id, String veiculo, String enderecoBase, Double capacidade, List<Coleta> coletas) {
        this.id = id;
        this.veiculo = veiculo;
        this.enderecoBase = enderecoBase;
        this.capacidade = capacidade;
        this.coletas = coletas;
    }

    @Override
    public String toString() {
        return "Rota{" +
                "id=" + id +
                ", veiculo='" + veiculo + '\'' +
                ", enderecoBase='" + enderecoBase + '\'' +
                ", capacidade=" + capacidade +
                ", coletas=" + coletas +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public String getEnderecoBase() {
        return enderecoBase;
    }

    public void setEnderecoBase(String enderecoBase) {
        this.enderecoBase = enderecoBase;
    }

    public Double getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Double capacidade) {
        this.capacidade = capacidade;
    }

    public List<Coleta> getColetas() {
        return coletas;
    }

    public void setColetas(List<Coleta> coletas) {
        this.coletas = coletas;
    }
}
