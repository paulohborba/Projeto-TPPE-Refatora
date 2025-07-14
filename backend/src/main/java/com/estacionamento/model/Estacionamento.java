package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;


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

    @ManyToMany(mappedBy = "estacionamentos", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private Set<Contratante> contratantes = new HashSet<>();
    
    public void addContratante(Contratante contratante) {
        if (contratante != null && !this.contratantes.contains(contratante)) {
            this.contratantes.add(contratante);
        }
    }

    public void removeContratante(Contratante contratante) {
        if (contratante != null && this.contratantes.contains(contratante)) {
            this.contratantes.remove(contratante);
        }
    }
}
