package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Estacionamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "contratantes")
public class Estacionamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "endereco", nullable = false, length = 255)
    private String endereco;

    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    @Column(name = "hora_abertura", nullable = false)
    private LocalTime horaAbertura;

    @Column(name = "hora_fechamento", nullable = false)
    private LocalTime horaFechamento;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "estacionamento_contratante",
            joinColumns = @JoinColumn(name = "estacionamento_id"),
            inverseJoinColumns = @JoinColumn(name = "contratante_id")
    )
    private Set<Contratante> contratantes = new HashSet<>();

    public void addContratante(Contratante contratante) {
        this.contratantes.add(contratante);
        contratante.getEstacionamentos().add(this);
    }

    public void removeContratante(Contratante contratante) {
        this.contratantes.remove(contratante);
        contratante.getEstacionamentos().remove(this);
    }
}