package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;


@Entity
@Table(name = "Diaria_Noturna")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiariaNoturna {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "adicional_noturno", precision = 10, scale = 2)
    private Double adicionalNoturno;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Indica que o ID desta entidade é mapeado do ID da entidade associada (Diaria)
    @JoinColumn(name = "id") // A coluna "id" é a FK para Diaria
    private Diaria diaria;
}
