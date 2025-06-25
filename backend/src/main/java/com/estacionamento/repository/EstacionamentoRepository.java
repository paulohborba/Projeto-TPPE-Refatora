package com.estacionamento.repository;

import com.estacionamento.model.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Long> {

    Object findByNome(String string);

}
