package com.estacionamento.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Diaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @OneToOne(mappedBy = "diaria", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private DiariaNoturna diariaNoturna;

    public void setDiariaNoturna(DiariaNoturna diariaNoturna) {
        if (diariaNoturna == null) {
            if (this.diariaNoturna != null) {
                this.diariaNoturna.setDiaria(null);
            }
        } else {
            diariaNoturna.setDiaria(this);
        }
        this.diariaNoturna = diariaNoturna;
    }
}