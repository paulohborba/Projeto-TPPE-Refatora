package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Veiculo;
import com.estacionamento.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public Veiculo criarVeiculo(Veiculo veiculo) {
        if (!StringUtils.hasText(veiculo.getPlaca())) {
            throw new DescricaoEmBrancoException("A placa do veículo não pode estar em branco.");
        }

        if (veiculoRepository.findByPlaca(veiculo.getPlaca()).isPresent()) {
            throw new IllegalArgumentException("Já existe um veículo cadastrado com a placa: " + veiculo.getPlaca());
        }

        return veiculoRepository.save(veiculo);
    }

    public Veiculo buscarVeiculoPorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Veículo com ID " + id + " não encontrado."));
    }

    public List<Veiculo> listarTodosVeiculos() {
        return veiculoRepository.findAll();
    }

    public Veiculo atualizarVeiculo(Long id, Veiculo veiculoAtualizado) {
        Veiculo veiculoExistente = veiculoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                    "Veículo com ID " + id + " não encontrado para atualização."
                ));

        if (!StringUtils.hasText(veiculoAtualizado.getPlaca())) {
            throw new DescricaoEmBrancoException("A placa do veículo não pode estar em branco.");
        }

        Optional<Veiculo> veiculoComMesmaPlaca = veiculoRepository.findByPlaca(veiculoAtualizado.getPlaca());
        if (veiculoComMesmaPlaca.isPresent() && !veiculoComMesmaPlaca.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                "Já existe outro veículo cadastrado com a placa: " + veiculoAtualizado.getPlaca()
            );
        }

        veiculoExistente.setPlaca(veiculoAtualizado.getPlaca());
        veiculoExistente.setMarca(veiculoAtualizado.getMarca());
        veiculoExistente.setModelo(veiculoAtualizado.getModelo());
        veiculoExistente.setCor(veiculoAtualizado.getCor());

        return veiculoRepository.save(veiculoExistente);
    }

    public void deletarVeiculo(Long id) {
        if (!veiculoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Veículo com ID " + id + " não encontrado para exclusão.");
        }
        veiculoRepository.deleteById(id);
    }
}