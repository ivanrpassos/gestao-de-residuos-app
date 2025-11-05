package br.com.fiap.gestao_residuos.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "LEITURA")
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LEITURA")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTENEDOR")
    private Contenedor contenedor;

    @Column(name = "DATA_HORA")
    private Date dataHora;

    @Column(name = "NIVEL_PERCENT")
    private Double nivelPercent;

    @Column(name = "PESO_KG")
    private Double pesoKg;

    public Leitura() { }

    public Leitura(Long id, Contenedor contenedor, Date dataHora, Double nivelPercent, Double pesoKg) {
        this.id = id;
        this.contenedor = contenedor;
        this.dataHora = dataHora;
        this.nivelPercent = nivelPercent;
        this.pesoKg = pesoKg;
    }

    @Override
    public String toString() {
        return "Leitura{" +
                "id=" + id +
                ", contenedor=" + contenedor +
                ", dataHora=" + dataHora +
                ", nivelPercent=" + nivelPercent +
                ", pesoKg=" + pesoKg +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contenedor getContenedor() {
        return contenedor;
    }

    public void setContenedor(Contenedor contenedor) {
        this.contenedor = contenedor;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public Double getNivelPercent() {
        return nivelPercent;
    }

    public void setNivelPercent(Double nivelPercent) {
        this.nivelPercent = nivelPercent;
    }

    public Double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }
}
