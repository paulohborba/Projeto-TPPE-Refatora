package com.estacionamento.repository;

import com.estacionamento.model.DiariaNoturna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiariaNoturnaRepository extends JpaRepository<DiariaNoturna, Long> {

}