package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Contratante;
import com.estacionamento.model.Estacionamento;
import com.estacionamento.model.Evento;
import com.estacionamento.repository.ContratanteRepository;
import com.estacionamento.repository.EstacionamentoRepository;
import com.estacionamento.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContratanteService {

    private final ContratanteRepository contratanteRepository;
    private final EstacionamentoRepository estacionamentoRepository;
    private final EventoRepository eventoRepository;

    public ContratanteService(ContratanteRepository contratanteRepository,
                              EstacionamentoRepository estacionamentoRepository,
                              EventoRepository eventoRepository) {
        this.contratanteRepository = contratanteRepository;
        this.estacionamentoRepository = estacionamentoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public Contratante criarContratante(Contratante contratante) {
        validarContratante(contratante);

        Optional<Contratante> contratanteExistenteCpfCnpj =
        contratanteRepository.findByCpfCnpj(contratante.getCpfCnpj());
        if (contratanteExistenteCpfCnpj.isPresent()) {
            throw new IllegalArgumentException(
                "Já existe um contratante com o CPF/CNPJ '" + contratante.getCpfCnpj() + "'."
            );
        }

        Optional<Contratante> contratanteExistenteEmail = contratanteRepository.findByEmail(contratante.getEmail());
        if (contratanteExistenteEmail.isPresent()) {
            throw new IllegalArgumentException(
                "Já existe um contratante com o e-mail '" + contratante.getEmail() + "'."
            );
        }

        Set<Estacionamento> managedEstacionamentos = new HashSet<>();
        if (contratante.getEstacionamentos() != null && !contratante.getEstacionamentos().isEmpty()) {
            for (Estacionamento e : contratante.getEstacionamentos()) {
                if (e.getId() != null) {
                    Estacionamento estacionamento = estacionamentoRepository.findById(e.getId())
                                    .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                        "Estacionamento com ID " + e.getId() + " não encontrado para associação."
                                    ));
                    managedEstacionamentos.add(estacionamento);
                } else {
                    throw new DescricaoEmBrancoException(
                        "ID do estacionamento não pode ser nulo ao associar a um contratante existente."
                    );
                }
            }
        }
        contratante.setEstacionamentos(new HashSet<>());
        managedEstacionamentos.forEach(contratante::addEstacionamento);

        Set<Evento> managedEventos = new HashSet<>();
        if (contratante.getEventos() != null && !contratante.getEventos().isEmpty()) {
            for (Evento ev : contratante.getEventos()) {
                if (ev.getId() != null) {
                    Evento evento = eventoRepository.findById(ev.getId())
                                    .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                        "Evento com ID " + ev.getId() + " não encontrado para associação."
                                    ));
                    managedEventos.add(evento);
                } else {
                    throw new DescricaoEmBrancoException(
                        "ID do evento não pode ser nulo ao associar a um contratante existente."
                    );
                }
            }
        }
        contratante.setEventos(new HashSet<>());
        managedEventos.forEach(contratante::addEvento);

        Contratante savedContratante = contratanteRepository.save(contratante);

        return savedContratante;
    }

    public Contratante buscarContratantePorId(Long id) {
        return contratanteRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Contratante com ID " + id + " não encontrado."));
    }

    public List<Contratante> listarTodosContratantes() {
        return contratanteRepository.findAll();
    }

    @Transactional
    public Contratante atualizarContratante(Long id, Contratante contratanteAtualizadoPayload) {
        Contratante contratanteExistente = contratanteRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Contratante não encontrado para atualização."
                ));

        validarContratante(contratanteAtualizadoPayload);

        Optional<Contratante> contratanteComMesmoCpfCnpj =
        contratanteRepository.findByCpfCnpj(contratanteAtualizadoPayload.getCpfCnpj());
        if (contratanteComMesmoCpfCnpj.isPresent() && !contratanteComMesmoCpfCnpj.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                "Já existe outro contratante com o CPF/CNPJ '" + contratanteAtualizadoPayload.getCpfCnpj() + "'."
            );
        }

        Optional<Contratante> contratanteComMesmoEmail =
        contratanteRepository.findByEmail(contratanteAtualizadoPayload.getEmail());
        if (contratanteComMesmoEmail.isPresent() && !contratanteComMesmoEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                "Já existe outro contratante com o e-mail '" + contratanteAtualizadoPayload.getEmail() + "'."
            );
        }

        contratanteExistente.setNome(contratanteAtualizadoPayload.getNome());
        contratanteExistente.setCpfCnpj(contratanteAtualizadoPayload.getCpfCnpj());
        contratanteExistente.setEmail(contratanteAtualizadoPayload.getEmail());
        contratanteExistente.setTelefone(contratanteAtualizadoPayload.getTelefone());

        Set<Long> novosEstacionamentoIdsNoPayload = contratanteAtualizadoPayload.getEstacionamentos() != null ?
            contratanteAtualizadoPayload.getEstacionamentos().stream()
                .map(Estacionamento::getId)
                .collect(Collectors.toSet()) : new HashSet<>();

        new HashSet<>(contratanteExistente.getEstacionamentos()).forEach(estAntigo -> {
            if (!novosEstacionamentoIdsNoPayload.contains(estAntigo.getId())) {
                contratanteExistente.removeEstacionamento(estAntigo);
            }
        });

        for (Long estId : novosEstacionamentoIdsNoPayload) {
            Estacionamento estacionamentoManaged = estacionamentoRepository.findById(estId)
                    .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        "Estacionamento com ID " + estId + " não encontrado para associação."
                    ));
            contratanteExistente.addEstacionamento(estacionamentoManaged);
        }

        Set<Long> novosEventoIdsNoPayload = contratanteAtualizadoPayload.getEventos() != null ?
            contratanteAtualizadoPayload.getEventos().stream()
                .map(Evento::getId)
                .collect(Collectors.toSet()) : new HashSet<>();

        new HashSet<>(contratanteExistente.getEventos()).forEach(evtAntigo -> {
            if (!novosEventoIdsNoPayload.contains(evtAntigo.getId())) {
                contratanteExistente.removeEvento(evtAntigo);
            }
        });

        for (Long evtId : novosEventoIdsNoPayload) {
            Evento eventoManaged = eventoRepository.findById(evtId)
                    .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        "Evento com ID " + evtId + " não encontrado para associação."
                    ));
            contratanteExistente.addEvento(eventoManaged);
        }

        return contratanteRepository.save(contratanteExistente);
    }

    @Transactional
    public void deletarContratante(Long id) {
        Contratante contratante = contratanteRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Contratante com ID " + id + " não encontrado para exclusão."
                ));

        new HashSet<>(contratante.getEstacionamentos()).forEach(estacionamento ->
            estacionamento.removeContratante(contratante)
        );

        new HashSet<>(contratante.getEventos()).forEach(evento ->
            evento.removeContratante(contratante)
        );

        contratanteRepository.delete(contratante);
    }

    private void validarContratante(Contratante contratante) {
        if (!StringUtils.hasText(contratante.getNome())) {
            throw new DescricaoEmBrancoException("O nome do contratante não pode estar em branco.");
        }
        if (!StringUtils.hasText(contratante.getCpfCnpj())) {
            throw new DescricaoEmBrancoException("O CPF/CNPJ do contratante não pode estar em branco.");
        }
        if (!StringUtils.hasText(contratante.getEmail())) {
            throw new DescricaoEmBrancoException("O e-mail do contratante não pode estar em branco.");
        }
    }
}