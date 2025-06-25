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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (!StringUtils.hasText(contratante.getNome())) {
            throw new DescricaoEmBrancoException("O nome do contratante não pode estar em branco.");
        }
        if (!StringUtils.hasText(contratante.getCpfCnpj())) {
            throw new DescricaoEmBrancoException("O CPF/CNPJ do contratante não pode estar em branco.");
        }
        if (!StringUtils.hasText(contratante.getEmail())) {
            throw new DescricaoEmBrancoException("O e-mail do contratante não pode estar em branco.");
        }

        if (contratanteRepository.findByCpfCnpj(contratante.getCpfCnpj()).isPresent()) {
            throw new IllegalArgumentException("Já existe um contratante com o CPF/CNPJ: " + contratante.getCpfCnpj());
        }
        if (contratanteRepository.findByEmail(contratante.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um contratante com o e-mail: " + contratante.getEmail());
        }

        List<Estacionamento> managedEstacionamentos = new ArrayList<>();
        if (contratante.getEstacionamentos() != null && !contratante.getEstacionamentos().isEmpty()) {
            for (Estacionamento est : contratante.getEstacionamentos()) {
                if (est.getId() == null) {
                    throw new IllegalArgumentException("Estacionamento com ID nulo associado ao contratante. Apenas estacionamentos existentes podem ser associados.");
                }
                Estacionamento estacionamentoExistente = estacionamentoRepository.findById(est.getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Estacionamento com ID " + est.getId() + " não encontrado para associar ao contratante."));
                managedEstacionamentos.add(estacionamentoExistente);
                estacionamentoExistente.addContratante(contratante);
            }
            contratante.setEstacionamentos(managedEstacionamentos);
        }

        List<Evento> managedEventos = new ArrayList<>();
        if (contratante.getEventos() != null && !contratante.getEventos().isEmpty()) {
            for (Evento evt : contratante.getEventos()) {
                if (evt.getId() == null) {
                    throw new IllegalArgumentException("Evento com ID nulo associado ao contratante. Apenas eventos existentes podem ser associados.");
                }
                Evento eventoExistente = eventoRepository.findById(evt.getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + evt.getId() + " não encontrado para associar ao contratante."));
                managedEventos.add(eventoExistente);
                eventoExistente.addContratante(contratante);
            }
            contratante.setEventos(managedEventos);
        }

        return contratanteRepository.save(contratante);
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

        if (!StringUtils.hasText(contratanteAtualizado.getNome())) {
            throw new DescricaoEmBrancoException("O nome do contratante não pode estar em branco.");
        }
        if (!StringUtils.hasText(contratanteAtualizado.getCpfCnpj())) {
            throw new DescricaoEmBrancoException("O CPF/CNPJ do contratante não pode estar em branco.");
        }
        if (!StringUtils.hasText(contratanteAtualizado.getEmail())) {
            throw new DescricaoEmBrancoException("O e-mail do contratante não pode estar em branco.");
        }

        Optional<Contratante> contratanteComMesmoCpfCnpj = contratanteRepository.findByCpfCnpj(contratanteAtualizado.getCpfCnpj());
        if (contratanteComMesmoCpfCnpj.isPresent() && !contratanteComMesmoCpfCnpj.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe outro contratante com o CPF/CNPJ: " + contratanteAtualizado.getCpfCnpj());
        }
        Optional<Contratante> contratanteComMesmoEmail = contratanteRepository.findByEmail(contratanteAtualizado.getEmail());
        if (contratanteComMesmoEmail.isPresent() && !contratanteComMesmoEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe outro contratante com o e-mail: " + contratanteAtualizado.getEmail());
        }

        contratanteExistente.setNome(contratanteAtualizado.getNome());
        contratanteExistente.setCpfCnpj(contratanteAtualizado.getCpfCnpj());
        contratanteExistente.setEmail(contratanteAtualizado.getEmail());
        contratanteExistente.setTelefone(contratanteAtualizado.getTelefone());

        if (contratanteExistente.getEstacionamentos() != null) {
            for (Estacionamento oldEst : new ArrayList<>(contratanteExistente.getEstacionamentos())) {
                oldEst.removeContratante(contratanteExistente);
            }
        }
        contratanteExistente.getEstacionamentos().clear();

        if (contratanteAtualizado.getEstacionamentos() != null && !contratanteAtualizado.getEstacionamentos().isEmpty()) {
            for (Estacionamento newEst : contratanteAtualizado.getEstacionamentos()) {
                if (newEst.getId() == null) {
                    throw new IllegalArgumentException("Estacionamento com ID nulo em atualização de contratante.");
                }
                Estacionamento estacionamentoExistente = estacionamentoRepository.findById(newEst.getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Estacionamento com ID " + newEst.getId() + " não encontrado para associar."));
                estacionamentoExistente.addContratante(contratanteExistente);
            }
        }

        if (contratanteExistente.getEventos() != null) {
            for (Evento oldEvt : new ArrayList<>(contratanteExistente.getEventos())) {
                oldEvt.removeContratante(contratanteExistente);
            }
        }
        contratanteExistente.getEventos().clear();

        if (contratanteAtualizado.getEventos() != null && !contratanteAtualizado.getEventos().isEmpty()) {
            for (Evento newEvt : contratanteAtualizado.getEventos()) {
                if (newEvt.getId() == null) {
                    throw new IllegalArgumentException("Evento com ID nulo em atualização de contratante.");
                }
                Evento eventoExistente = eventoRepository.findById(newEvt.getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Evento com ID " + newEvt.getId() + " não encontrado para associar."));
                contratanteExistente.addEvento(eventoExistente);
            }
        }

        return contratanteRepository.save(contratanteExistente);
    }

    @Transactional
    public void deletarContratante(Long id) {
        Contratante contratanteParaDeletar = contratanteRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Contratante com ID " + id + " não encontrado para exclusão."));

        if (contratanteParaDeletar.getEstacionamentos() != null) {
            for (Estacionamento estacionamento : new ArrayList<>(contratanteParaDeletar.getEstacionamentos())) {
                estacionamento.removeContratante(contratanteParaDeletar);
            }
        }

        if (contratanteParaDeletar.getEventos() != null) {
            for (Evento evento : new ArrayList<>(contratanteParaDeletar.getEventos())) {
                evento.removeContratante(contratanteParaDeletar);
            }
        }

        contratanteRepository.delete(contratanteParaDeletar);
    }
}