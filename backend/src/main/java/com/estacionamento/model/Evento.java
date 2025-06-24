package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;


@Entity
@Table(name = "Evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToMany(mappedBy = "eventos")
    private List<Contratante> contratantes = new ArrayList<>();

    // Métodos utilitários para gerenciar coleções (boa prática)
    public void addContratante(Contratante contratante) {
        this.contratantes.add(contratante);
        if (contratante.getEventos() == null) {
            contratante.setEventos(new ArrayList<>());
        }
        contratante.getEventos().add(this);
    }

    public void removeContratante(Contratante contratante) {
        this.contratantes.remove(contratante);
        contratante.getEventos().remove(this);
    }
}