package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Contratante;
import com.estacionamento.model.Evento;
import com.estacionamento.repository.ContratanteRepository;
import com.estacionamento.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final ContratanteRepository contratanteRepository;

    public EventoService(EventoRepository eventoRepository, ContratanteRepository contratanteRepository) {
        this.eventoRepository = eventoRepository;
        this.contratanteRepository = contratanteRepository;
    }

    @Transactional
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

        Set<Contratante> managedContratantes = new HashSet<>();
        if (evento.getContratantes() != null && !evento.getContratantes().isEmpty()) {
            for (Contratante c : evento.getContratantes()) {
                if (c.getId() != null) {
                    Contratante contratante = contratanteRepository.findById(c.getId())
                            .orElseThrow(() -> new ObjetoNaoEncontradoException("Contratante com ID " + c.getId() + " não encontrado."));
                    managedContratantes.add(contratante);
                } else {
                    throw new DescricaoEmBrancoException("ID do contratante não pode ser nulo ao associar a um evento existente.");
                }
            }
        }
        evento.setContratantes(managedContratantes);

        Evento savedEvento = eventoRepository.save(evento);
        savedEvento.getContratantes().forEach(contratante -> contratante.getEventos().add(savedEvento));

        return savedEvento;
    }

    public Evento buscarEventoPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + id + " não encontrado."));
    }

    public List<Evento> listarTodosEventos() {
        return eventoRepository.findAll();
    }

    @Transactional
    public Evento atualizarEvento(Long id, Evento eventoAtualizado) {
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + id + " não encontrado para atualização."));

        if (!StringUtils.hasText(eventoAtualizado.getNomeEvento())) {
            throw new DescricaoEmBrancoException("O nome do evento não pode estar em branco na atualização.");
        }
        if (eventoAtualizado.getDataInicio() == null) {
            throw new DescricaoEmBrancoException("A data de início do evento não pode ser nula na atualização.");
        }
        if (eventoAtualizado.getDataFim() == null) {
            throw new DescricaoEmBrancoException("A data de fim do evento não pode ser nula na atualização.");
        }
        if (eventoAtualizado.getDataFim().isBefore(eventoAtualizado.getDataInicio())) {
            throw new IllegalArgumentException("A data de fim do evento não pode ser anterior à data de início na atualização.");
        }

        eventoExistente.setNomeEvento(eventoAtualizado.getNomeEvento());
        eventoExistente.setDescricao(eventoAtualizado.getDescricao());
        eventoExistente.setDataInicio(eventoAtualizado.getDataInicio());
        eventoExistente.setDataFim(eventoAtualizado.getDataFim());

        Set<Contratante> newManagedContratantes = new HashSet<>();
        if (eventoAtualizado.getContratantes() != null) {
            for (Contratante c : eventoAtualizado.getContratantes()) {
                if (c.getId() != null) {
                    Contratante contratante = contratanteRepository.findById(c.getId())
                            .orElseThrow(() -> new ObjetoNaoEncontradoException("Contratante com ID " + c.getId() + " não encontrado para associação."));
                    newManagedContratantes.add(contratante);
                } else {
                    throw new DescricaoEmBrancoException("ID do contratante não pode ser nulo ao associar a um evento existente.");
                }
            }
        }

        // Remove associações antigas que não estão mais na lista atualizada
        Set<Contratante> contratantesParaRemover = new HashSet<>(eventoExistente.getContratantes());
        contratantesParaRemover.removeAll(newManagedContratantes);
        contratantesParaRemover.forEach(contratante -> contratante.removeEvento(eventoExistente)); // CORREÇÃO AQUI

        // Adiciona novas associações ou mantém as existentes
        newManagedContratantes.forEach(contratante -> {
            if (!eventoExistente.getContratantes().contains(contratante)) {
                contratante.addEvento(eventoExistente); // CORREÇÃO AQUI
            }
        });
        eventoExistente.setContratantes(newManagedContratantes); // Sincroniza a coleção do evento

        return eventoRepository.save(eventoExistente);
    }

    @Transactional
    public void deletarEvento(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + id + " não encontrado para exclusão."));

        Set<Contratante> contratantesCopia = new HashSet<>(evento.getContratantes());
        contratantesCopia.forEach(contratante -> contratante.removeEvento(evento)); // CORREÇÃO AQUI

        eventoRepository.delete(evento);
    }
}