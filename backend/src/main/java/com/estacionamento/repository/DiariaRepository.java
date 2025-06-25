package com.estacionamento.repository;

import com.estacionamento.model.Diaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiariaRepository extends JpaRepository<Diaria, Long> {
}