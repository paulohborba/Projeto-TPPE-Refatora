package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Diaria;
import com.estacionamento.model.DiariaNoturna;
import com.estacionamento.repository.DiariaNoturnaRepository;
import com.estacionamento.repository.DiariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DiariaService {

    private final DiariaRepository diariaRepository;
    private final DiariaNoturnaRepository diariaNoturnaRepository;

    public DiariaService(DiariaRepository diariaRepository,
                         DiariaNoturnaRepository diariaNoturnaRepository) {
        this.diariaRepository = diariaRepository;
        this.diariaNoturnaRepository = diariaNoturnaRepository;
    }

    @Transactional
    public Diaria criarDiaria(Diaria diaria) {
        if (diaria.getValor() == null ||
            diaria.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DescricaoEmBrancoException("O valor da diária deve ser maior que zero.");
        }
        if (!StringUtils.hasText(diaria.getTipo())) {
            throw new DescricaoEmBrancoException("O tipo da diária não pode estar em branco.");
        }

        if (diaria.getDiariaNoturna() != null) {
            DiariaNoturna diariaNoturna = diaria.getDiariaNoturna();
            if (diariaNoturna.getHoraInicio() == null ||
                diariaNoturna.getHoraFim() == null) {
                throw new DescricaoEmBrancoException(
                    "Hora de início e fim da diária noturna não podem ser nulas.");
            }
            if (diariaNoturna.getAdicionalNoturno() == null ||
                diariaNoturna.getAdicionalNoturno().compareTo(BigDecimal.ZERO) < 0) {
                throw new DescricaoEmBrancoException(
                    "Adicional noturno não pode ser nulo ou negativo.");
            }
        }

        Diaria savedDiaria = diariaRepository.save(diaria);
        if (diaria.getDiariaNoturna() != null) {
            diaria.getDiariaNoturna().setDiaria(savedDiaria);
            diariaNoturnaRepository.save(diaria.getDiariaNoturna());
        }
        return savedDiaria;
    }

    public List<Diaria> listarTodasDiarias() {
        return diariaRepository.findAll();
    }

    public Diaria buscarDiariaPorId(Long id) {
        return diariaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Diária com ID " + id + " não encontrada."));
    }

    @Transactional
    public Diaria atualizarDiaria(Long id, Diaria diariaAtualizada) {
        Diaria diariaExistente = diariaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Diária com ID " + id + " não encontrada para atualização."));

        if (diariaAtualizada.getValor() == null ||
            diariaAtualizada.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DescricaoEmBrancoException(
                "O valor da diária deve ser maior que zero na atualização.");
        }
        if (!StringUtils.hasText(diariaAtualizada.getTipo())) {
            throw new DescricaoEmBrancoException(
                "O tipo da diária não pode estar em branco na atualização.");
        }

        diariaExistente.setValor(diariaAtualizada.getValor());
        diariaExistente.setTipo(diariaAtualizada.getTipo());
        diariaExistente.setDescricao(diariaAtualizada.getDescricao());

        if (diariaAtualizada.getDiariaNoturna() != null) {
            DiariaNoturna novaDiariaNoturna = diariaAtualizada.getDiariaNoturna();

            if (novaDiariaNoturna.getHoraInicio() == null ||
                novaDiariaNoturna.getHoraFim() == null) {
                throw new DescricaoEmBrancoException(
                    "Hora de início e fim da diária noturna não podem ser nulas.");
            }
            if (novaDiariaNoturna.getAdicionalNoturno() == null ||
                novaDiariaNoturna.getAdicionalNoturno().compareTo(BigDecimal.ZERO) < 0) {
                throw new DescricaoEmBrancoException(
                    "Adicional noturno não pode ser nulo ou negativo.");
            }
            
            if (diariaExistente.getDiariaNoturna() != null) {
                DiariaNoturna existenteDiariaNoturna = diariaExistente.getDiariaNoturna();
                existenteDiariaNoturna.setHoraInicio(novaDiariaNoturna.getHoraInicio());
                existenteDiariaNoturna.setHoraFim(novaDiariaNoturna.getHoraFim());
                existenteDiariaNoturna.setAdicionalNoturno(novaDiariaNoturna.getAdicionalNoturno());
                diariaNoturnaRepository.save(existenteDiariaNoturna);
            } else {
                novaDiariaNoturna.setDiaria(diariaExistente);
                diariaExistente.setDiariaNoturna(novaDiariaNoturna);
                diariaNoturnaRepository.save(novaDiariaNoturna);
            }
        } else {
            if (diariaExistente.getDiariaNoturna() != null) {
                diariaNoturnaRepository.delete(diariaExistente.getDiariaNoturna());
                diariaExistente.setDiariaNoturna(null);
            }
        }

        return diariaRepository.save(diariaExistente);
    }

    @Transactional
    public void deletarDiaria(Long id) {
        Diaria diaria = diariaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Diária com ID " + id + " não encontrada para exclusão."));

        diariaRepository.delete(diaria);
    }
}