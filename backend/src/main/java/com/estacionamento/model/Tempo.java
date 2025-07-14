package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "Tempo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Tempo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duracao", nullable = false)
    private LocalTime duracao;

    @Column(name = "valor_fracao", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFracao;

    @Column(name = "desconto", precision = 10, scale = 2)
    private BigDecimal desconto;
}
