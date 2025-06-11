package com.estacionamento_tppe.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Estacionamento")
public class Estacionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // INT no SQL geralmente mapeia para Integer

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "endereco", nullable = false, length = 255)
    private String endereco;

    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    // Relacionamento Many-to-Many com Contratante (Gerenciado por Estacionamento)
    @ManyToMany
    @JoinTable(
        name = "Estacionamento_Contratante",
        joinColumns = @JoinColumn(name = "estacionamento_id"),
        inverseJoinColumns = @JoinColumn(name = "contratante_id")
    )
    private Set<Contratante> contratantes = new HashSet<>();

    // Construtor padrão (necessário para JPA)
    public Estacionamento() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }
    public Set<Contratante> getContratantes() { return contratantes; }
    public void setContratantes(Set<Contratante> contratantes) { this.contratantes = contratantes; }
}
