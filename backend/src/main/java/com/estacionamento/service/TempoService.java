package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Tempo;
import com.estacionamento.repository.TempoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TempoService {

    private final TempoRepository tempoRepository;

    public TempoService(TempoRepository tempoRepository) {
        this.tempoRepository = tempoRepository;
    }

    public Tempo criarTempo(Tempo tempo) {
        if (tempo.getValorFracao() == null || tempo.getValorFracao().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DescricaoEmBrancoException("O valor da fração de tempo não pode ser nulo ou menor/igual a zero.");
        }

        tempo.setValorFracao(tempo.getValorFracao().setScale(2, RoundingMode.HALF_UP));
        if (tempo.getDesconto() != null) {
            tempo.setDesconto(tempo.getDesconto().setScale(2, RoundingMode.HALF_UP));
        } else {
            tempo.setDesconto(BigDecimal.ZERO);
        }

        return tempoRepository.save(tempo);
    }

    public Tempo buscarTempoPorId(Long id) {
        return tempoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Tempo com ID " + id + " não encontrada."));
    }

    public List<Tempo> listarTodosTempos() {
        return tempoRepository.findAll();
    }

    public Tempo atualizarTempo(Long id, Tempo tempoAtualizado) {
        Tempo tempoExistente = tempoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Tempo com ID " + id + " não encontrada para atualização."));

        if (tempoAtualizado.getValorFracao() == null || tempoAtualizado.getValorFracao().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DescricaoEmBrancoException("O valor da fração de tempo não pode ser nulo ou menor/igual a zero.");
        }

        tempoExistente.setDuracao(tempoAtualizado.getDuracao());
        tempoExistente.setValorFracao(tempoAtualizado.getValorFracao().setScale(2, RoundingMode.HALF_UP));
        if (tempoAtualizado.getDesconto() != null) {
            tempoExistente.setDesconto(tempoAtualizado.getDesconto().setScale(2, RoundingMode.HALF_UP));
        } else {
            tempoExistente.setDesconto(BigDecimal.ZERO);
        }

        return tempoRepository.save(tempoExistente);
    }

    public void deletarTempo(Long id) {
        if (!tempoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Configuração de Tempo com ID " + id + " não encontrada para exclusão.");
        }
        tempoRepository.deleteById(id);
    }
}