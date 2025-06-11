package com.estacionamento_tppe.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Estacionamento")
public class Estacionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "endereco", nullable = false, length = 255)
    private String endereco;

    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    @ManyToMany
    @JoinTable(
        name = "Estacionamento_Contratante",
        joinColumns = @JoinColumn(name = "estacionamento_id"),
        inverseJoinColumns = @JoinColumn(name = "contratante_id")
    )
    private Set<Contratante> contratantes = new HashSet<>();

    public Estacionamento() {}

    public Estacionamento(Integer id, String nome, String endereco, Integer capacidade, Set<Contratante> contratantes) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.capacidade = capacidade;
        this.contratantes = contratantes;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public Set<Contratante> getContratantes() {
        return contratantes;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public void setContratantes(Set<Contratante> contratantes) {
        this.contratantes = contratantes;
    }

    @Override
    public String toString() {
        return "Estacionamento{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", endereco='" + endereco + '\'' +
               ", capacidade=" + capacidade +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estacionamento that = (Estacionamento) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}