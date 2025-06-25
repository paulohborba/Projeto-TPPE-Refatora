package com.estacionamento.repository;

import com.estacionamento.model.Tempo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempoRepository extends JpaRepository<Tempo, Long> {
    
}