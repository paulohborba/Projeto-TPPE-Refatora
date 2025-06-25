package com.estacionamento.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Mensalista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensalista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "periodo_meses")
    private Integer periodoMeses;

    @Column(name = "descricao", length = 255)
    private String descricao;
}