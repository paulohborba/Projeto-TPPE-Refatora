package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

     @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "estacionamento_contratante",
            joinColumns = @JoinColumn(name = "contratante_id"),
            inverseJoinColumns = @JoinColumn(name = "estacionamento_id")
    )
    @JsonIgnore
    @ToString.Exclude
    private Set<Estacionamento> estacionamentos = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "contratante_evento",
            joinColumns = @JoinColumn(name = "contratante_id"),
            inverseJoinColumns = @JoinColumn(name = "evento_id")
    )
    @JsonIgnore
    @ToString.Exclude
    private Set<Evento> eventos = new HashSet<>();


    public void addEstacionamento(Estacionamento estacionamento) {
        if (estacionamento != null && !this.estacionamentos.contains(estacionamento)) {
            this.estacionamentos.add(estacionamento);
                estacionamento.addContratante(this); 
        }
    }

    public void removeEstacionamento(Estacionamento estacionamento) {
        if (estacionamento != null && this.estacionamentos.contains(estacionamento)) {
            this.estacionamentos.remove(estacionamento);
                estacionamento.removeContratante(this);
        }
    }

    public void addEvento(Evento evento) {
        if (evento != null && !this.eventos.contains(evento)) {
            this.eventos.add(evento);
                evento.addContratante(this);
        }
    }

    public void removeEvento(Evento evento) {
        if (evento != null && this.eventos.contains(evento)) {
            this.eventos.remove(evento);
                evento.removeContratante(this);
        }
    }
}
