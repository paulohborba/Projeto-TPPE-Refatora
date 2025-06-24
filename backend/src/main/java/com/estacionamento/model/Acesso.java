package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Acesso")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tempo_id")
    private Tempo tempo;

    @Column(name = "entrada", nullable = false)
    private LocalDateTime entrada;

    @Column(name = "saida")
    private LocalDateTime saida;

    @Column(name = "valor_cobrado", precision = 10, scale = 2)
    private Double valorCobrado;

    @Column(name = "tipo_acesso", nullable = false, length = 50)
    private String tipoAcesso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diaria_id")
    private Diaria diaria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensalista_id")
    private Mensalista mensalista;
}