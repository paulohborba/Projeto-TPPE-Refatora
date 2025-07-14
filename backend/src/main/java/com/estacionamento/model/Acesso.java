package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Acesso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"estacionamento", "veiculo", "tempo", "diaria", "mensalista"})
public class Acesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacionamento_id", nullable = false)
    private Estacionamento estacionamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Column(name = "valor_cobrado", precision = 10, scale = 2)
    private BigDecimal valorCobrado;

    @Column(name = "tipo_acesso", nullable = false, length = 50)
    private String tipoAcesso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tempo_id")
    private Tempo tempo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diaria_id")
    private Diaria diaria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensalista_id")
    private Mensalista mensalista;
}