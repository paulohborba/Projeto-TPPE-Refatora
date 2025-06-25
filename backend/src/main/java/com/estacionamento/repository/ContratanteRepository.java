package com.estacionamento.repository;

import com.estacionamento.model.Contratante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContratanteRepository extends JpaRepository<Contratante, Long> {

    Optional<Contratante> findByCpfCnpj(String cpfCnpj);

    Optional<Contratante> findByEmail(String email);
}