package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;


@Entity
@Table(name = "Diaria_Noturna")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiariaNoturna {
    @Id
    private Long id;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "adicional_noturno", precision = 10, scale = 2)
    private BigDecimal adicionalNoturno;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Diaria diaria;
}
