package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @ManyToMany(mappedBy = "eventos", fetch = FetchType.LAZY)
    private Set<Contratante> contratantes = new HashSet<>();

    public void addContratante(Contratante contratante) {
        this.contratantes.add(contratante);
        contratante.getEventos().add(this);
    }

    public void removeContratante(Contratante contratante) {
        this.contratantes.remove(contratante);
        contratante.getEventos().remove(this);
    }

    public Evento(long l, String string, String string2, LocalDateTime now, LocalDateTime plusHours, @SuppressWarnings("rawtypes") HashSet hashSet) {
    }
}