package br.com.fiap.gestao_residuos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CONTENEDOR")
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONTENEDOR")
    private Long id;

    @Column(name = "LOCALIZACAO")
    private String localizacao;

    @Column(name = "CAPACIDADE_LITROS")
    private Double capacidadeLitros;

    @Column(name = "TIPO_MATERIAL")
    private String tipoMaterial;

    public Contenedor() { }

    public Contenedor(Long id, String localizacao, Double capacidadeLitros, String tipoMaterial) {
        this.id = id;
        this.localizacao = localizacao;
        this.capacidadeLitros = capacidadeLitros;
        this.tipoMaterial = tipoMaterial;
    }

    @Override
    public String toString() {
        return "Contenedor{" +
                "id=" + id +
                ", localizacao='" + localizacao + '\'' +
                ", capacidadeLitros=" + capacidadeLitros +
                ", tipoMaterial='" + tipoMaterial + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Double getCapacidadeLitros() {
        return capacidadeLitros;
    }

    public void setCapacidadeLitros(Double capacidadeLitros) {
        this.capacidadeLitros = capacidadeLitros;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }
}
