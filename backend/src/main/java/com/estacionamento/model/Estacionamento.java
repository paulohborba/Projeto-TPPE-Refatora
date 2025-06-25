package com.estacionamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Estacionamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estacionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "endereco", nullable = false, length = 255)
    private String endereco;

    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    @ManyToMany(mappedBy = "estacionamentos")
    private List<Contratante> contratantes = new ArrayList<>();

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Acesso> acessos = new ArrayList<>();

    public void addContratante(Contratante contratante) {
        this.contratantes.add(contratante);
        if (contratante.getEstacionamentos() == null) {
            contratante.setEstacionamentos(new ArrayList<>());
        }
        contratante.getEstacionamentos().add(this);
    }

    public void removeContratante(Contratante contratante) {
        this.contratantes.remove(contratante);
        contratante.getEstacionamentos().remove(this);
    }

    public void addAcesso(Acesso acesso) {
        this.acessos.add(acesso);
        acesso.setEstacionamento(this);
    }

    public void removeAcesso(Acesso acesso) {
        this.acessos.remove(acesso);
        acesso.setEstacionamento(null);
    }
}