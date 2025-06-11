
package com.estacionamento_tppe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Estacionamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}