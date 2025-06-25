package com.estacionamento.repository;

import com.estacionamento.model.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Long> {
    Optional<Estacionamento> findByNome(String nome);
}