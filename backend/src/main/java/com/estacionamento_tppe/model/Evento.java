package com.estacionamento_tppe.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Evento")

public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacionamento_id", nullable = false)
    private Estacionamento estacionamento;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;

    @Column(name = "descricao")
    private String descricao;

    @ManyToMany(mappedBy = "eventos")
    private Set<Contratante> contratantes = new HashSet<>();

    public Evento() {}

    public Evento(Integer id, Estacionamento estacionamento, String nome, LocalDateTime dataInicio, LocalDateTime dataFim, String descricao, Set<Contratante> contratantes) {
        this.id = id;
        this.estacionamento = estacionamento;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.descricao = descricao;
        this.contratantes = contratantes;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Estacionamento getEstacionamento() {
        return estacionamento;
    }

    public String getNome() {
        return nome;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public String getDescricao() {
        return descricao;
    }

    public Set<Contratante> getContratantes() {
        return contratantes;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setEstacionamento(Estacionamento estacionamento) {
        this.estacionamento = estacionamento;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setContratantes(Set<Contratante> contratantes) {
        this.contratantes = contratantes;
    }

    @Override
    public String toString() {
        return "Evento{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", dataInicio=" + dataInicio +
               ", dataFim=" + dataFim +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return id != null && id.equals(evento.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}