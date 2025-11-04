package br.com.fiap.gestao_residuos.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DESTINACAO")
public class Destinacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DESTINACAO")
    private Long id;

    @Column(name = "TIPO_MATERIAL", length = 50)
    private String tipoMaterial;

    @Column(name = "LOCAL_DESTINO", length = 100)
    private String localDestino;

    @Column(name = "DATA_REGISTRO")
    private Date dataRegistro;

    @Column(name = "QUANTIDADE_KG")
    private Double quantidadeKg;

    // getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getLocalDestino() {
        return localDestino;
    }

    public void setLocalDestino(String localDestino) {
        this.localDestino = localDestino;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Double getQuantidadeKg() {
        return quantidadeKg;
    }

    public void setQuantidadeKg(Double quantidadeKg) {
        this.quantidadeKg = quantidadeKg;
    }

    // construtores

    public Destinacao() {
    }

    public Destinacao(Long id, String tipoMaterial, String localDestino, Date dataRegistro, Double quantidadeKg) {
        this.id = id;
        this.tipoMaterial = tipoMaterial;
        this.localDestino = localDestino;
        this.dataRegistro = dataRegistro;
        this.quantidadeKg = quantidadeKg;
    }
}
