package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Contratante;
import com.estacionamento.model.Estacionamento;
import com.estacionamento.repository.ContratanteRepository;
import com.estacionamento.repository.EstacionamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EstacionamentoService {

    private final EstacionamentoRepository estacionamentoRepository;
    private final ContratanteRepository contratanteRepository;

    public EstacionamentoService(
        EstacionamentoRepository estacionamentoRepository, ContratanteRepository contratanteRepository) {
        this.estacionamentoRepository = estacionamentoRepository;
        this.contratanteRepository = contratanteRepository;
    }

    @Transactional
    public Estacionamento criarEstacionamento(Estacionamento estacionamento) {
        if (!StringUtils.hasText(estacionamento.getNome())) {
            throw new DescricaoEmBrancoException("O nome do estacionamento não pode estar em branco.");
        }
        if (!StringUtils.hasText(estacionamento.getEndereco())) {
            throw new DescricaoEmBrancoException("O endereço do estacionamento não pode estar em branco.");
        }
        if (estacionamento.getCapacidade() == null || estacionamento.getCapacidade() <= 0) {
            throw new IllegalArgumentException("A capacidade do estacionamento deve ser maior que zero.");
        }
        if (estacionamento.getHoraAbertura() == null) {
            throw new DescricaoEmBrancoException("A hora de abertura não pode ser nula.");
        }
        if (estacionamento.getHoraFechamento() == null) {
            throw new DescricaoEmBrancoException("A hora de fechamento não pode ser nula.");
        }
        if (estacionamento.getHoraFechamento().isBefore(estacionamento.getHoraAbertura())) {
            throw new IllegalArgumentException("A hora de fechamento não pode ser anterior à hora de abertura.");
        }

        Optional<Estacionamento> estacionamentoExistente = 
        estacionamentoRepository.findByNome(estacionamento.getNome());
        if (estacionamentoExistente.isPresent()) {
            throw new IllegalArgumentException(
                "Já existe um estacionamento com o nome '" + estacionamento.getNome() + "'."
            );
        }

        Set<Contratante> managedContratantes = new HashSet<>();
        if (estacionamento.getContratantes() != null && !estacionamento.getContratantes().isEmpty()) {
            for (Contratante c : estacionamento.getContratantes()) {
                if (c.getId() != null) {
                    Contratante contratante = contratanteRepository.findById(c.getId())
                            .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                "Contratante com ID " + c.getId() + " não encontrado."
                            ));
                    managedContratantes.add(contratante);
                } else {
                    throw new DescricaoEmBrancoException(
                        "ID do contratante não pode ser nulo ao associar a um estacionamento existente."
                    );
                }
            }
        }
        estacionamento.setContratantes(managedContratantes);

        Estacionamento savedEstacionamento = estacionamentoRepository.save(estacionamento);
        savedEstacionamento.getContratantes().forEach(contratante -> 
        contratante.getEstacionamentos().add(savedEstacionamento));

        return savedEstacionamento;
    }

    public Estacionamento buscarEstacionamentoPorId(Long id) {
        return estacionamentoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Estacionamento com ID " + id + " não encontrado."
                ));
    }

    public List<Estacionamento> listarTodosEstacionamentos() {
        return estacionamentoRepository.findAll();
    }

    @Transactional
    public Estacionamento atualizarEstacionamento(Long id, Estacionamento estacionamentoAtualizado) {
        Estacionamento estacionamentoExistente = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Estacionamento com ID " + id + " não encontrado para atualização."
                ));

        if (!StringUtils.hasText(estacionamentoAtualizado.getNome())) {
            throw new DescricaoEmBrancoException("O nome do estacionamento não pode estar em branco na atualização.");
        }
        if (!StringUtils.hasText(estacionamentoAtualizado.getEndereco())) {
            throw new DescricaoEmBrancoException(
                "O endereço do estacionamento não pode estar em branco na atualização."
            );
        }
        if (estacionamentoAtualizado.getCapacidade() == null || estacionamentoAtualizado.getCapacidade() <= 0) {
            throw new IllegalArgumentException(
                "A capacidade do estacionamento deve ser maior que zero na atualização."
            );
        }
        if (estacionamentoAtualizado.getHoraAbertura() == null) {
            throw new DescricaoEmBrancoException("A hora de abertura não pode ser nula na atualização.");
        }
        if (estacionamentoAtualizado.getHoraFechamento() == null) {
            throw new DescricaoEmBrancoException("A hora de fechamento não pode ser nula na atualização.");
        }
        if (estacionamentoAtualizado.getHoraFechamento().isBefore(estacionamentoAtualizado.getHoraAbertura())) {
            throw new IllegalArgumentException(
                "A hora de fechamento não pode ser anterior à hora de abertura na atualização."
            );
        }

        Optional<Estacionamento> estacionamentoComMesmoNome = 
        estacionamentoRepository.findByNome(estacionamentoAtualizado.getNome());
        if (estacionamentoComMesmoNome.isPresent() && !estacionamentoComMesmoNome.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                "Já existe outro estacionamento com o nome '" + estacionamentoAtualizado.getNome() + "'."
            );
        }

        estacionamentoExistente.setNome(estacionamentoAtualizado.getNome());
        estacionamentoExistente.setEndereco(estacionamentoAtualizado.getEndereco());
        estacionamentoExistente.setCapacidade(estacionamentoAtualizado.getCapacidade());
        estacionamentoExistente.setHoraAbertura(estacionamentoAtualizado.getHoraAbertura());
        estacionamentoExistente.setHoraFechamento(estacionamentoAtualizado.getHoraFechamento());

        Set<Contratante> newManagedContratantes = new HashSet<>();
        if (estacionamentoAtualizado.getContratantes() != null) {
            for (Contratante c : estacionamentoAtualizado.getContratantes()) {
                if (c.getId() != null) {
                    Contratante contratante = contratanteRepository.findById(c.getId())
                            .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                "Contratante com ID " + c.getId() + " não encontrado para associação."
                            ));
                    newManagedContratantes.add(contratante);
                } else {
                    throw new DescricaoEmBrancoException(
                        "ID do contratante não pode ser nulo ao associar a um estacionamento existente."
                    );
                }
            }
        }

        Set<Contratante> contratantesParaRemover = new HashSet<>(estacionamentoExistente.getContratantes());
        contratantesParaRemover.removeAll(newManagedContratantes);
        contratantesParaRemover.forEach(contratante -> contratante.removeEstacionamento(estacionamentoExistente));

        newManagedContratantes.forEach(contratante -> {
            if (!estacionamentoExistente.getContratantes().contains(contratante)) {
                contratante.addEstacionamento(estacionamentoExistente);
            }
        });
        estacionamentoExistente.setContratantes(newManagedContratantes);

        return estacionamentoRepository.save(estacionamentoExistente);
    }

    @Transactional
    public void deletarEstacionamento(Long id) {
        Estacionamento estacionamento = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Estacionamento com ID " + id + " não encontrado para exclusão."
                ));

        Set<Contratante> contratantesCopia = new HashSet<>(estacionamento.getContratantes());
        contratantesCopia.forEach(contratante -> contratante.removeEstacionamento(estacionamento));

        estacionamentoRepository.delete(estacionamento);
    }
}
