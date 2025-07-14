

package com.estacionamento.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "Diaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "diariaNoturna")
@ToString(exclude = "diariaNoturna")
public class Diaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "tipo", length = 50, nullable = false, unique = true)
    private String tipo;

    @Column(length = 255)
    private String descricao;

    @OneToOne(mappedBy = "diaria", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private DiariaNoturna diariaNoturna;

    public Diaria(BigDecimal valor, String tipo, String descricao) {
        this.valor = valor;
        this.tipo = tipo;
        this.descricao = descricao;
    }

    public void setDiariaNoturna(DiariaNoturna diariaNoturna) {
        if (this.diariaNoturna != null) {
            this.diariaNoturna.setDiaria(null);
        }

        this.diariaNoturna = diariaNoturna;

        if (diariaNoturna != null) {
            diariaNoturna.setDiaria(this);
        }
    }

    public void addDiariaNoturna(DiariaNoturna diariaNoturna) {
        setDiariaNoturna(diariaNoturna);
    }

    public void removeDiariaNoturna() {
        setDiariaNoturna(null);
    }

    public boolean hasNightRate() {
        return diariaNoturna != null;
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = valor;
        if (hasNightRate() && diariaNoturna.getAdicionalNoturno() != null) {
            total = total.add(diariaNoturna.getAdicionalNoturno());
        }
        return total;
    }
}
