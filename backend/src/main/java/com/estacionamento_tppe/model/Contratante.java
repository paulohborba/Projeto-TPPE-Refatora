package com.estacionamento_tppe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Contratante")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

}