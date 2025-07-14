package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Mensalista;
import com.estacionamento.repository.MensalistaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MensalistaService {

    private final MensalistaRepository mensalistaRepository;

    public MensalistaService(MensalistaRepository mensalistaRepository) {
        this.mensalistaRepository = mensalistaRepository;
    }

    public Mensalista criarMensalista(Mensalista mensalista) {
        if (mensalista.getValor() == null || mensalista.getValor().compareTo(BigDecimal.ZERO) <= 0) { 
            throw new DescricaoEmBrancoException("O valor da mensalidade não pode ser nulo ou menor/igual a zero.");
        }
        if (mensalista.getPeriodoMeses() != null && mensalista.getPeriodoMeses() <= 0) {
            throw new DescricaoEmBrancoException("O período de meses deve ser um valor positivo.");
        }

        mensalista.setValor(mensalista.getValor().setScale(2, RoundingMode.HALF_UP));

        return mensalistaRepository.save(mensalista);
    }

    public Mensalista buscarMensalistaPorId(Long id) {
        return mensalistaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Configuração de Mensalista com ID " + id + " não encontrada."
                ));
    }

    public List<Mensalista> listarTodosMensalistas() {
        return mensalistaRepository.findAll();
    }

    public Mensalista atualizarMensalista(Long id, Mensalista mensalistaAtualizado) {
        Mensalista mensalistaExistente = mensalistaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Configuração de Mensalista com ID " + id + " não encontrada para atualização."
                ));

        if (mensalistaAtualizado.getValor() == null || 
        mensalistaAtualizado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DescricaoEmBrancoException("O valor da mensalidade não pode ser nulo ou menor/igual a zero.");
        }
        if (mensalistaAtualizado.getPeriodoMeses() != null && mensalistaAtualizado.getPeriodoMeses() <= 0) {
            throw new DescricaoEmBrancoException("O período de meses deve ser um valor positivo.");
        }

        mensalistaExistente.setValor(mensalistaAtualizado.getValor().setScale(2, RoundingMode.HALF_UP));
        mensalistaExistente.setPeriodoMeses(mensalistaAtualizado.getPeriodoMeses());
        mensalistaExistente.setDescricao(mensalistaAtualizado.getDescricao());

        return mensalistaRepository.save(mensalistaExistente);
    }

    public void deletarMensalista(Long id) {
        if (!mensalistaRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException(
                "Configuração de Mensalista com ID " + id + " não encontrada para exclusão."
            );
        }
        mensalistaRepository.deleteById(id);
    }
}