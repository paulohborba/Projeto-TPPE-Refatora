package com.estacionamento.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "Diaria_Noturna")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "diaria")
@ToString(exclude = "diaria")
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
    @JsonBackReference
    private Diaria diaria;

    public DiariaNoturna(LocalTime horaInicio, LocalTime horaFim, BigDecimal adicionalNoturno) {
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.adicionalNoturno = adicionalNoturno;
    }

    public void setDiaria(Diaria diaria) {
        this.diaria = diaria;
    }

    public boolean isValidTimeRange() {
        return horaInicio != null && horaFim != null && horaInicio.isBefore(horaFim);
    }

    public boolean isTimeInNightPeriod(LocalTime time) {
        if (!isValidTimeRange() || time == null) {
            return false;
        }
        if (horaInicio.isBefore(horaFim)) {
            return !time.isBefore(horaInicio) && !time.isAfter(horaFim);
        } else {
            return !time.isBefore(horaInicio) || !time.isAfter(horaFim);
        }
    }

    public BigDecimal getAdicionalNoturnoOrZero() {
        return adicionalNoturno != null ? adicionalNoturno : BigDecimal.ZERO;
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        if (adicionalNoturno != null && adicionalNoturno.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Adicional noturno não pode ser negativo");
        }
    }
}