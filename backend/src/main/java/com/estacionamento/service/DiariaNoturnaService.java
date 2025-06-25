package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.DiariaNoturna;
import com.estacionamento.repository.DiariaNoturnaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DiariaNoturnaService {

    private final DiariaNoturnaRepository diariaNoturnaRepository;

    public DiariaNoturnaService(DiariaNoturnaRepository diariaNoturnaRepository) {
        this.diariaNoturnaRepository = diariaNoturnaRepository;
    }

    public DiariaNoturna buscarDiariaNoturnaPorId(Long id) {
        return diariaNoturnaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Diária Noturna com ID " + id + " não encontrada."));
    }

    public List<DiariaNoturna> listarTodasDiariasNoturnas() {
        return diariaNoturnaRepository.findAll();
    }

    @Transactional
    public DiariaNoturna atualizarDiariaNoturna(Long id, DiariaNoturna diariaNoturnaAtualizada) {
        DiariaNoturna diariaNoturnaExistente = diariaNoturnaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Diária Noturna com ID " + id + " não encontrada para atualização."));

        if (diariaNoturnaAtualizada.getHoraInicio() == null) {
            throw new DescricaoEmBrancoException("A hora de início da diária noturna não pode ser nula.");
        }
        if (diariaNoturnaAtualizada.getHoraFim() == null) {
            throw new DescricaoEmBrancoException("A hora de fim da diária noturna não pode ser nula.");
        }
        if (diariaNoturnaAtualizada.getHoraFim().isBefore(diariaNoturnaAtualizada.getHoraInicio())) {
            throw new IllegalArgumentException("A hora de fim da diária noturna não pode ser anterior à hora de início.");
        }
        if (diariaNoturnaAtualizada.getAdicionalNoturno() != null && diariaNoturnaAtualizada.getAdicionalNoturno().compareTo(BigDecimal.ZERO) < 0) {
             throw new IllegalArgumentException("O adicional noturno não pode ser negativo.");
        }

        diariaNoturnaExistente.setHoraInicio(diariaNoturnaAtualizada.getHoraInicio());
        diariaNoturnaExistente.setHoraFim(diariaNoturnaAtualizada.getHoraFim());
        diariaNoturnaExistente.setAdicionalNoturno(diariaNoturnaAtualizada.getAdicionalNoturno());

        return diariaNoturnaRepository.save(diariaNoturnaExistente);
    }

    public void deletarDiariaNoturna(Long id) {
        if (!diariaNoturnaRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Configuração de Diária Noturna com ID " + id + " não encontrada para exclusão.");
        }
        diariaNoturnaRepository.deleteById(id);
    }
}