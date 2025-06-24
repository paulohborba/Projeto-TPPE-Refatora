package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Contratante")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contratante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "cpf_cnpj", unique = true, nullable = false, length = 20)
    private String cpfCnpj;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @ManyToMany(mappedBy = "contratantes")
    private List<Estacionamento> estacionamentos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "Contratante_Evento",
        joinColumns = @JoinColumn(name = "contratante_id"),
        inverseJoinColumns = @JoinColumn(name = "evento_id")
    )
    private List<Evento> eventos = new ArrayList<>();

    // Métodos utilitários para gerenciar coleções (boa prática)
    public void addEstacionamento(Estacionamento estacionamento) {
        this.estacionamentos.add(estacionamento);
        if (estacionamento.getContratantes() == null) {
            estacionamento.setContratantes(new ArrayList<>());
        }
        estacionamento.getContratantes().add(this);
    }

    public void removeEstacionamento(Estacionamento estacionamento) {
        this.estacionamentos.remove(estacionamento);
        estacionamento.getContratantes().remove(this);
    }

    public void addEvento(Evento evento) {
        this.eventos.add(evento);
        if (evento.getContratantes() == null) {
            evento.setContratantes(new ArrayList<>());
        }
        evento.getContratantes().add(this);
    }

    public void removeEvento(Evento evento) {
        this.eventos.remove(evento);
        evento.getContratantes().remove(this);
    }
}