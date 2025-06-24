package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;

@Entity
@Table(name = "Tempo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tempo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duracao")
    private LocalTime duracao;

    @Column(name = "valor_fracao", nullable = false, precision = 10, scale = 2)
    private Double valorFracao;

    @Column(name = "desconto", precision = 5, scale = 2)
    private Double desconto;
}
