package com.estacionamento.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Mensalista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode 
public class Mensalista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "periodo_meses", nullable = false)
    private Integer periodoMeses;

    @Column(length = 255)
    private String descricao;
}