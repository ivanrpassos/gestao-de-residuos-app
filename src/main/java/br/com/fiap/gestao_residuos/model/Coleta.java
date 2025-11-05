package br.com.fiap.gestao_residuos.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "COLETA")
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COLETA")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTENEDOR")
    private Contenedor contenedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ROTA")
    private Rota rota;

    @Column(name = "DATA_AGENDADA")
    private Date dataAgendada;

    @Column(name = "STATUS", length = 100)
    private String status;

    public Coleta() { }

    public Coleta(Long id, Contenedor contenedor, Rota rota, Date dataAgendada, String status) {
        this.id = id;
        this.contenedor = contenedor;
        this.rota = rota;
        this.dataAgendada = dataAgendada;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Coleta{" +
                "id=" + id +
                ", contenedor=" + contenedor +
                ", rota=" + rota +
                ", dataAgendada=" + dataAgendada +
                ", status='" + status + '\'' +
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

    public Rota getRota() {
        return rota;
    }

    public void setRota(Rota rota) {
        this.rota = rota;
    }

    public Date getDataAgendada() {
        return dataAgendada;
    }

    public void setDataAgendada(Date dataAgendada) {
        this.dataAgendada = dataAgendada;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
