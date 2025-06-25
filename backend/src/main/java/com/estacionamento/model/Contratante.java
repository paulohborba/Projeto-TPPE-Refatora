package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Contratante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"estacionamentos", "eventos"}) 
public class Contratante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 20)
    private String cpfCnpj;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @ManyToMany(mappedBy = "contratantes", fetch = FetchType.LAZY)
    private Set<Estacionamento> estacionamentos = new HashSet<>();

    @ManyToMany(mappedBy = "contratantes", fetch = FetchType.LAZY)
    private Set<Evento> eventos = new HashSet<>();

    public void addEstacionamento(Estacionamento estacionamento) {
        this.estacionamentos.add(estacionamento);
        estacionamento.getContratantes().add(this);
    }

    public void removeEstacionamento(Estacionamento estacionamento) {
        this.estacionamentos.remove(estacionamento);
        estacionamento.getContratantes().remove(this);
    }

    public void addEvento(Evento evento) {
        this.eventos.add(evento);
        evento.getContratantes().add(this);
    }

    public void removeEvento(Evento evento) {
        this.eventos.remove(evento);
        evento.getContratantes().remove(this);
    }
}