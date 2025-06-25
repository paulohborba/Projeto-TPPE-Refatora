package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Diaria;
import com.estacionamento.model.DiariaNoturna;
import com.estacionamento.repository.DiariaRepository;
import com.estacionamento.repository.DiariaNoturnaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class DiariaService {

    private final DiariaRepository diariaRepository;
    public DiariaService(DiariaRepository diariaRepository, DiariaNoturnaRepository diariaNoturnaRepository) {
        this.diariaRepository = diariaRepository;
    }

    @Transactional
    public Diaria criarDiaria(Diaria diaria) {
        if (diaria.getValor() == null || diaria.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DescricaoEmBrancoException("O valor da diária não pode ser nulo ou menor/igual a zero.");
        }

        diaria.setValor(diaria.getValor().setScale(2, RoundingMode.HALF_UP));

        Diaria savedDiaria = diariaRepository.save(diaria);

        return savedDiaria;
    }

    public Diaria buscarDiariaPorId(Long id) {
        return diariaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Diária com ID " + id + " não encontrada."));
    }

    public List<Diaria> listarTodasDiarias() {
        return diariaRepository.findAll();
    }

    @Transactional
    public Diaria atualizarDiaria(Long id, Diaria diariaAtualizada) {
        Diaria diariaExistente = diariaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Diária com ID " + id + " não encontrada para atualização."));

        if (diariaAtualizada.getValor() == null || diariaAtualizada.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DescricaoEmBrancoException("O valor da diária não pode ser nulo ou menor/igual a zero.");
        }

        diariaExistente.setValor(diariaAtualizada.getValor().setScale(2, RoundingMode.HALF_UP));
        diariaExistente.setTipo(diariaAtualizada.getTipo());
        diariaExistente.setDescricao(diariaAtualizada.getDescricao());

        if (diariaAtualizada.getDiariaNoturna() != null) {
            DiariaNoturna newDiariaNoturnaData = diariaAtualizada.getDiariaNoturna();

            if (newDiariaNoturnaData.getHoraInicio() == null || newDiariaNoturnaData.getHoraFim() == null) {
                throw new DescricaoEmBrancoException("Hora de início e fim da diária noturna não podem ser nulas.");
            }
            if (newDiariaNoturnaData.getHoraFim().isBefore(newDiariaNoturnaData.getHoraInicio())) {
                throw new IllegalArgumentException("Hora de fim da diária noturna não pode ser anterior à hora de início.");
            }
            if (newDiariaNoturnaData.getAdicionalNoturno() != null && newDiariaNoturnaData.getAdicionalNoturno().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("O adicional noturno não pode ser negativo.");
            }
            newDiariaNoturnaData.setAdicionalNoturno(newDiariaNoturnaData.getAdicionalNoturno().setScale(2, RoundingMode.HALF_UP));

            if (diariaExistente.getDiariaNoturna() == null) {
                diariaExistente.setDiariaNoturna(newDiariaNoturnaData);
            } else {
                DiariaNoturna existingDiariaNoturna = diariaExistente.getDiariaNoturna();
                existingDiariaNoturna.setHoraInicio(newDiariaNoturnaData.getHoraInicio());
                existingDiariaNoturna.setHoraFim(newDiariaNoturnaData.getHoraFim());
                existingDiariaNoturna.setAdicionalNoturno(newDiariaNoturnaData.getAdicionalNoturno());
            }
        } else {
            if (diariaExistente.getDiariaNoturna() != null) {
                diariaExistente.setDiariaNoturna(null);
            }
        }

        return diariaRepository.save(diariaExistente);
    }

    @Transactional
    public void deletarDiaria(Long id) {
        if (!diariaRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Diária com ID " + id + " não encontrada para exclusão.");
        }
        diariaRepository.deleteById(id);
    }
}