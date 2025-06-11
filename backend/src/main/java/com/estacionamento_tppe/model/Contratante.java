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
@Table(name = "Contratante")

public class Contratante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "cpf_cnpj", unique = true, nullable = false, length = 20)
    private String cpfCnpj;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @ManyToMany(mappedBy = "contratantes")
    private Set<Estacionamento> estacionamentos = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "Contratante_Evento",
        joinColumns = @JoinColumn(name = "contratante_id"),
        inverseJoinColumns = @JoinColumn(name = "evento_id")
    )
    private Set<Evento> eventos = new HashSet<>();

    public Contratante() {}

    public Contratante(Integer id, String nome, String cpfCnpj, String email, String telefone, Set<Estacionamento> estacionamentos, Set<Evento> eventos) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.telefone = telefone;
        this.estacionamentos = estacionamentos;
        this.eventos = eventos;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public Set<Estacionamento> getEstacionamentos() {
        return estacionamentos;
    }

    public Set<Evento> getEventos() {
        return eventos;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEstacionamentos(Set<Estacionamento> estacionamentos) {
        this.estacionamentos = estacionamentos;
    }

    public void setEventos(Set<Evento> eventos) {
        this.eventos = eventos;
    }

    @Override
    public String toString() {
        return "Contratante{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", cpfCnpj='" + cpfCnpj + '\'' +
               ", email='" + email + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contratante that = (Contratante) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}