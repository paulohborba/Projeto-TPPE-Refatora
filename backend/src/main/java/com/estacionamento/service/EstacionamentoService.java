package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Estacionamento;
import com.estacionamento.repository.EstacionamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class EstacionamentoService {

    private final EstacionamentoRepository estacionamentoRepository;

    public EstacionamentoService(EstacionamentoRepository estacionamentoRepository) {
        this.estacionamentoRepository = estacionamentoRepository;
    }

    public Estacionamento criarEstacionamento(Estacionamento estacionamento) {
        if (!StringUtils.hasText(estacionamento.getNome())) {
            throw new DescricaoEmBrancoException("O nome do estacionamento não pode estar em branco.");
        }
        if (!StringUtils.hasText(estacionamento.getEndereco())) {
            throw new DescricaoEmBrancoException("O endereço do estacionamento não pode estar em branco.");
        }
        if (estacionamento.getCapacidade() == null || estacionamento.getCapacidade() <= 0) {
            throw new DescricaoEmBrancoException("A capacidade do estacionamento deve ser um valor positivo.");
        }

        return estacionamentoRepository.save(estacionamento);
    }

    public Estacionamento buscarEstacionamentoPorId(Long id) {
        return estacionamentoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Estacionamento com ID " + id + " não encontrado."));
    }

    public List<Estacionamento> listarTodosEstacionamentos() {
        return estacionamentoRepository.findAll();
    }

    public Estacionamento atualizarEstacionamento(Long id, Estacionamento estacionamentoAtualizado) {
        Estacionamento estacionamentoExistente = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Estacionamento com ID " + id + " não encontrado para atualização."));

        if (!StringUtils.hasText(estacionamentoAtualizado.getNome())) {
            throw new DescricaoEmBrancoException("O nome do estacionamento não pode estar em branco.");
        }
        if (!StringUtils.hasText(estacionamentoAtualizado.getEndereco())) {
            throw new DescricaoEmBrancoException("O endereço do estacionamento não pode estar em branco.");
        }
        if (estacionamentoAtualizado.getCapacidade() == null || estacionamentoAtualizado.getCapacidade() <= 0) {
            throw new DescricaoEmBrancoException("A capacidade do estacionamento deve ser um valor positivo.");
        }

        estacionamentoExistente.setNome(estacionamentoAtualizado.getNome());
        estacionamentoExistente.setEndereco(estacionamentoAtualizado.getEndereco());
        estacionamentoExistente.setCapacidade(estacionamentoAtualizado.getCapacidade());

        return estacionamentoRepository.save(estacionamentoExistente);
    }

    public void deletarEstacionamento(Long id) {
        if (!estacionamentoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Estacionamento com ID " + id + " não encontrado para exclusão.");
        }
        estacionamentoRepository.deleteById(id);
    }
}