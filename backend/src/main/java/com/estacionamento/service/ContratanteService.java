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

        // Valida unicidade de CPF/CNPJ
        Optional<Contratante> contratanteExistenteCpfCnpj = contratanteRepository.findByCpfCnpj(contratante.getCpfCnpj());
        if (contratanteExistenteCpfCnpj.isPresent()) {
            throw new IllegalArgumentException("Já existe um contratante com o CPF/CNPJ '" + contratante.getCpfCnpj() + "'.");
        }

        // Valida unicidade de E-mail
        Optional<Contratante> contratanteExistenteEmail = contratanteRepository.findByEmail(contratante.getEmail());
        if (contratanteExistenteEmail.isPresent()) {
            throw new IllegalArgumentException("Já existe um contratante com o e-mail '" + contratante.getEmail() + "'.");
        }

        Set<Estacionamento> managedEstacionamentos = new HashSet<>();
        if (contratante.getEstacionamentos() != null && !contratante.getEstacionamentos().isEmpty()) {
            for (Estacionamento e : contratante.getEstacionamentos()) {
                if (e.getId() != null) {
                    Estacionamento estacionamento = estacionamentoRepository.findById(e.getId())
                            .orElseThrow(() -> new ObjetoNaoEncontradoException("Estacionamento com ID " + e.getId() + " não encontrado."));
                    managedEstacionamentos.add(estacionamento);
                } else {
                    throw new DescricaoEmBrancoException("ID do estacionamento não pode ser nulo ao associar a um contratante existente.");
                }
            }
        }
        contratante.setEstacionamentos(managedEstacionamentos);

        Set<Evento> managedEventos = new HashSet<>();
        if (contratante.getEventos() != null && !contratante.getEventos().isEmpty()) {
            for (Evento ev : contratante.getEventos()) {
                if (ev.getId() != null) {
                    Evento evento = eventoRepository.findById(ev.getId())
                            .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + ev.getId() + " não encontrado."));
                    managedEventos.add(evento);
                } else {
                    throw new DescricaoEmBrancoException("ID do evento não pode ser nulo ao associar a um contratante existente.");
                }
            }
        }
        contratante.setEventos(managedEventos);

        Contratante savedContratante = contratanteRepository.save(contratante);

        savedContratante.getEstacionamentos().forEach(estacionamento -> estacionamento.getContratantes().add(savedContratante));
        savedContratante.getEventos().forEach(evento -> evento.getContratantes().add(savedContratante));

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
    public Contratante atualizarContratante(Long id, Contratante contratanteAtualizado) {
        Contratante contratanteExistente = contratanteRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Contratante com ID " + id + " não encontrado para atualização."));

        validarContratante(contratanteAtualizado);

        // Valida unicidade de CPF/CNPJ (excluindo o próprio contratante)
        Optional<Contratante> contratanteComMesmoCpfCnpj = contratanteRepository.findByCpfCnpj(contratanteAtualizado.getCpfCnpj());
        if (contratanteComMesmoCpfCnpj.isPresent() && !contratanteComMesmoCpfCnpj.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe outro contratante com o CPF/CNPJ '" + contratanteAtualizado.getCpfCnpj() + "'.");
        }

        // Valida unicidade de E-mail (excluindo o próprio contratante)
        Optional<Contratante> contratanteComMesmoEmail = contratanteRepository.findByEmail(contratanteAtualizado.getEmail());
        if (contratanteComMesmoEmail.isPresent() && !contratanteComMesmoEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe outro contratante com o e-mail '" + contratanteAtualizado.getEmail() + "'.");
        }

        contratanteExistente.setNome(contratanteAtualizado.getNome());
        contratanteExistente.setCpfCnpj(contratanteAtualizado.getCpfCnpj());
        contratanteExistente.setEmail(contratanteAtualizado.getEmail());
        contratanteExistente.setTelefone(contratanteAtualizado.getTelefone());

        // --- Atualiza associações de estacionamentos ---
        Set<Estacionamento> newManagedEstacionamentos = new HashSet<>();
        if (contratanteAtualizado.getEstacionamentos() != null) {
            for (Estacionamento e : contratanteAtualizado.getEstacionamentos()) {
                if (e.getId() != null) {
                    Estacionamento estacionamento = estacionamentoRepository.findById(e.getId())
                            .orElseThrow(() -> new ObjetoNaoEncontradoException("Estacionamento com ID " + e.getId() + " não encontrado para associação."));
                    newManagedEstacionamentos.add(estacionamento);
                } else {
                    throw new DescricaoEmBrancoException("ID do estacionamento não pode ser nulo ao associar a um contratante existente.");
                }
            }
        }

        // Remove associações antigas que não estão mais na lista atualizada
        Set<Estacionamento> estacionamentosParaRemover = new HashSet<>(contratanteExistente.getEstacionamentos());
        estacionamentosParaRemover.removeAll(newManagedEstacionamentos);
        estacionamentosParaRemover.forEach(estacionamento -> estacionamento.removeContratante(contratanteExistente)); // CORREÇÃO AQUI

        // Adiciona novas associações ou mantém as existentes
        newManagedEstacionamentos.forEach(estacionamento -> {
            if (!contratanteExistente.getEstacionamentos().contains(estacionamento)) {
                estacionamento.addContratante(contratanteExistente); // CORREÇÃO AQUI
            }
        });
        contratanteExistente.setEstacionamentos(newManagedEstacionamentos); // Sincroniza a coleção do contratante


        // --- Atualiza associações de eventos ---
        Set<Evento> newManagedEventos = new HashSet<>();
        if (contratanteAtualizado.getEventos() != null) {
            for (Evento ev : contratanteAtualizado.getEventos()) {
                if (ev.getId() != null) {
                    Evento evento = eventoRepository.findById(ev.getId())
                            .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + ev.getId() + " não encontrado para associação."));
                    newManagedEventos.add(evento);
                } else {
                    throw new DescricaoEmBrancoException("ID do evento não pode ser nulo ao associar a um contratante existente.");
                }
            }
        }

        // Remove associações antigas que não estão mais na lista atualizada
        Set<Evento> eventosParaRemover = new HashSet<>(contratanteExistente.getEventos());
        eventosParaRemover.removeAll(newManagedEventos);
        eventosParaRemover.forEach(evento -> evento.removeContratante(contratanteExistente)); // CORREÇÃO AQUI

        // Adiciona novas associações ou mantém as existentes
        newManagedEventos.forEach(evento -> {
            if (!contratanteExistente.getEventos().contains(evento)) {
                evento.addContratante(contratanteExistente); // CORREÇÃO AQUI
            }
        });
        contratanteExistente.setEventos(newManagedEventos); // Sincroniza a coleção do contratante


        return contratanteRepository.save(contratanteExistente);
    }

    @Transactional
    public void deletarContratante(Long id) {
        Contratante contratante = contratanteRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Contratante com ID " + id + " não encontrado para exclusão."));

        // Desassociar de estacionamentos
        Set<Estacionamento> estacionamentosCopia = new HashSet<>(contratante.getEstacionamentos());
        estacionamentosCopia.forEach(estacionamento -> estacionamento.removeContratante(contratante)); // CORREÇÃO AQUI

        // Desassociar de eventos
        Set<Evento> eventosCopia = new HashSet<>(contratante.getEventos());
        eventosCopia.forEach(evento -> evento.removeContratante(contratante)); // CORREÇÃO AQUI

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