package com.estacionamento.repository;

import com.estacionamento.model.Mensalista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensalistaRepository extends JpaRepository<Mensalista, Long> {

}
