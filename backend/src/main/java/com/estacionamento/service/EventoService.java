package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Contratante;
import com.estacionamento.model.Evento;
import com.estacionamento.repository.ContratanteRepository;
import com.estacionamento.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final ContratanteRepository contratanteRepository;

    public EventoService(EventoRepository eventoRepository, ContratanteRepository contratanteRepository) {
        this.eventoRepository = eventoRepository;
        this.contratanteRepository = contratanteRepository;
    }

    public Evento criarEvento(Evento evento) {
        if (!StringUtils.hasText(evento.getNomeEvento())) {
            throw new DescricaoEmBrancoException("O nome do evento não pode estar em branco.");
        }
        if (evento.getDataInicio() == null) {
            throw new DescricaoEmBrancoException("A data de início do evento não pode ser nula.");
        }
        if (evento.getDataFim() == null) {
            throw new DescricaoEmBrancoException("A data de fim do evento não pode ser nula.");
        }

        if (evento.getDataFim().isBefore(evento.getDataInicio())) {
            throw new IllegalArgumentException("A data de fim do evento não pode ser anterior à data de início.");
        }

        List<Contratante> managedContratantes = new ArrayList<>();
        if (evento.getContratantes() != null && !evento.getContratantes().isEmpty()) {
            for (Contratante c : evento.getContratantes()) {
                if (c.getId() == null) {
                    throw new IllegalArgumentException("Contratante com ID nulo associado ao evento. Apenas contratantes existentes podem ser associados.");
                }
                Contratante contratanteExistente = contratanteRepository.findById(c.getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Contratante com ID " + c.getId() + " não encontrado para associar ao evento."));
                managedContratantes.add(contratanteExistente);
            }
            evento.setContratantes(managedContratantes);
            for (Contratante mc : managedContratantes) {
                mc.addEvento(evento);
            }
        }

        return eventoRepository.save(evento);
    }

    public Evento buscarEventoPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + id + " não encontrado."));
    }

    public List<Evento> listarTodosEventos() {
        return eventoRepository.findAll();
    }

    public Evento atualizarEvento(Long id, Evento eventoAtualizado) {
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + id + " não encontrado para atualização."));

        if (!StringUtils.hasText(eventoAtualizado.getNomeEvento())) {
            throw new DescricaoEmBrancoException("O nome do evento não pode estar em branco.");
        }
        if (eventoAtualizado.getDataInicio() == null) {
            throw new DescricaoEmBrancoException("A data de início do evento não pode ser nula.");
        }
        if (eventoAtualizado.getDataFim() == null) {
            throw new DescricaoEmBrancoException("A data de fim do evento não pode ser nula.");
        }
        if (eventoAtualizado.getDataFim().isBefore(eventoAtualizado.getDataInicio())) {
            throw new IllegalArgumentException("A data de fim do evento não pode ser anterior à data de início.");
        }

        eventoExistente.setNomeEvento(eventoAtualizado.getNomeEvento());
        eventoExistente.setDataInicio(eventoAtualizado.getDataInicio());
        eventoExistente.setDataFim(eventoAtualizado.getDataFim());
        eventoExistente.setDescricao(eventoAtualizado.getDescricao());

        if (eventoAtualizado.getContratantes() != null) {
            if (eventoExistente.getContratantes() != null) {
                for (Contratante oldContratante : eventoExistente.getContratantes()) {
                    oldContratante.removeEvento(eventoExistente);
                }
            }

            List<Contratante> newManagedContratantes = new ArrayList<>();
            for (Contratante c : eventoAtualizado.getContratantes()) {
                if (c.getId() == null) {
                    throw new IllegalArgumentException("Contratante com ID nulo em atualização de evento.");
                }
                Contratante contratanteExistente = contratanteRepository.findById(c.getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Contratante com ID " + c.getId() + " não encontrado para associar ao evento."));
                newManagedContratantes.add(contratanteExistente);
                contratanteExistente.addEvento(eventoExistente);
            }
            eventoExistente.setContratantes(newManagedContratantes);
        }

        return eventoRepository.save(eventoExistente);
    }

    public void deletarEvento(Long id) {
        Evento eventoParaDeletar = eventoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + id + " não encontrado para exclusão."));

        if (eventoParaDeletar.getContratantes() != null) {
            for (Contratante contratante : eventoParaDeletar.getContratantes()) {
                contratante.removeEvento(eventoParaDeletar);
            }
        }
        eventoRepository.delete(eventoParaDeletar);
    }
}