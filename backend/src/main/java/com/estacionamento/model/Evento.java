package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "contratantes")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_evento", nullable = false, length = 100)
    private String nomeEvento;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(columnDefinition = "TEXT")
    private String descricao;

       @ManyToMany(mappedBy = "eventos", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private Set<Contratante> contratantes = new HashSet<>();

    public void addContratante(Contratante contratante) {
        if (contratante != null && !this.contratantes.contains(contratante)) {
            this.contratantes.add(contratante);
        }
    }

    public void removeContratante(Contratante contratante) {
        if (contratante != null && this.contratantes.contains(contratante)) { 
            this.contratantes.remove(contratante);
        }
    }
}