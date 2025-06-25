package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.exception.ValorAcessoInvalidoException;
import com.estacionamento.model.*;
import com.estacionamento.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AcessoService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final AcessoRepository acessoRepository;
    private final EstacionamentoRepository estacionamentoRepository;
    private final VeiculoRepository veiculoRepository;
    private final TempoRepository tempoRepository;
    private final DiariaRepository diariaRepository;
    private final MensalistaRepository mensalistaRepository;

    public AcessoService(AcessoRepository acessoRepository,
                         EstacionamentoRepository estacionamentoRepository,
                         VeiculoRepository veiculoRepository,
                         TempoRepository tempoRepository,
                         DiariaRepository diariaRepository,
                         MensalistaRepository mensalistaRepository) {
        this.acessoRepository = acessoRepository;
        this.estacionamentoRepository = estacionamentoRepository;
        this.veiculoRepository = veiculoRepository;
        this.tempoRepository = tempoRepository;
        this.diariaRepository = diariaRepository;
        this.mensalistaRepository = mensalistaRepository;
    }

    @Transactional
    public Acesso criarAcesso(Acesso acesso) {
        if (acesso.getEntrada() == null) {
            throw new DescricaoEmBrancoException("A hora de entrada do acesso não pode ser nula.");
        }
        if (!StringUtils.hasText(acesso.getTipoAcesso())) {
            throw new DescricaoEmBrancoException("O tipo de acesso não pode estar em branco.");
        }
        if (acesso.getEstacionamento() == null || acesso.getEstacionamento().getId() == null) {
            throw new DescricaoEmBrancoException("O estacionamento do acesso não pode ser nulo e deve ter um ID.");
        }
        if (acesso.getVeiculo() == null || acesso.getVeiculo().getId() == null) {
            throw new DescricaoEmBrancoException("O veículo do acesso não pode ser nulo e deve ter um ID.");
        }

        Estacionamento estacionamento = estacionamentoRepository.findById(acesso.getEstacionamento().getId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Estacionamento com ID " + acesso.getEstacionamento().getId() + " não encontrado."));
        acesso.setEstacionamento(estacionamento);

        Veiculo veiculo = veiculoRepository.findById(acesso.getVeiculo().getId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Veículo com ID " + acesso.getVeiculo().getId() + " não encontrado."));
        acesso.setVeiculo(veiculo);

        switch (acesso.getTipoAcesso().toUpperCase()) {
            case "TEMPO":
                if (acesso.getTempo() == null || acesso.getTempo().getId() == null) {
                    throw new DescricaoEmBrancoException("Para o tipo 'TEMPO', é necessário fornecer o ID da configuração de tempo.");
                }
                Tempo tempo = tempoRepository.findById(acesso.getTempo().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Tempo com ID " + acesso.getTempo().getId() + " não encontrada."));
                acesso.setTempo(tempo);
                break;
            case "DIARIA":
                if (acesso.getDiaria() == null || acesso.getDiaria().getId() == null) {
                    throw new DescricaoEmBrancoException("Para o tipo 'DIARIA', é necessário fornecer o ID da configuração de diária.");
                }
                Diaria diaria = diariaRepository.findById(acesso.getDiaria().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Diária com ID " + acesso.getDiaria().getId() + " não encontrada."));
                acesso.setDiaria(diaria);
                break;
            case "MENSALISTA":
                if (acesso.getMensalista() == null || acesso.getMensalista().getId() == null) {
                    throw new DescricaoEmBrancoException("Para o tipo 'MENSALISTA', é necessário fornecer o ID da configuração de mensalista.");
                }
                Mensalista mensalista = mensalistaRepository.findById(acesso.getMensalista().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Mensalista com ID " + acesso.getMensalista().getId() + " não encontrada."));
                acesso.setMensalista(mensalista);
                break;
            default:
                throw new ValorAcessoInvalidoException("Tipo de acesso inválido: " + acesso.getTipoAcesso() + ". Tipos aceitos: TEMPO, DIARIA, MENSALISTA.");
        }

        if (acesso.getSaida() != null) {
            acesso.setValorCobrado(calcularValorAcesso(acesso));
        } else {
            acesso.setValorCobrado(BigDecimal.ZERO);
        }

        return acessoRepository.save(acesso);
    }

    public Acesso buscarAcessoPorId(Long id) {
        return acessoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Acesso com ID " + id + " não encontrado."));
    }

    public List<Acesso> listarTodosAcessos() {
        return acessoRepository.findAll();
    }

    @Transactional
    public Acesso atualizarAcesso(Long id, Acesso acessoAtualizado) {
        Acesso acessoExistente = acessoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Acesso com ID " + id + " não encontrado para atualização."));

        if (acessoAtualizado.getEntrada() == null) {
            throw new DescricaoEmBrancoException("A hora de entrada do acesso não pode ser nula na atualização.");
        }
        if (!StringUtils.hasText(acessoAtualizado.getTipoAcesso())) {
            throw new DescricaoEmBrancoException("O tipo de acesso não pode estar em branco na atualização.");
        }

        acessoExistente.setEntrada(acessoAtualizado.getEntrada());
        acessoExistente.setTipoAcesso(acessoAtualizado.getTipoAcesso());

        if (acessoAtualizado.getSaida() != null) {
            if (acessoAtualizado.getSaida().isBefore(acessoExistente.getEntrada())) {
                throw new IllegalArgumentException("A hora de saída não pode ser anterior à hora de entrada.");
            }
            acessoExistente.setSaida(acessoAtualizado.getSaida());
            acessoExistente.setValorCobrado(calcularValorAcesso(acessoExistente));
        } else {
            acessoExistente.setSaida(null);
            acessoExistente.setValorCobrado(BigDecimal.ZERO);
        }

        acessoExistente.setTempo(null);
        acessoExistente.setDiaria(null);
        acessoExistente.setMensalista(null);

        switch (acessoExistente.getTipoAcesso().toUpperCase()) {
            case "TEMPO":
                if (acessoAtualizado.getTempo() == null || acessoAtualizado.getTempo().getId() == null) {
                    throw new DescricaoEmBrancoException("Para o tipo 'TEMPO', é necessário fornecer o ID da configuração de tempo na atualização.");
                }
                Tempo tempo = tempoRepository.findById(acessoAtualizado.getTempo().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Tempo com ID " + acessoAtualizado.getTempo().getId() + " não encontrada para atualização."));
                acessoExistente.setTempo(tempo);
                break;
            case "DIARIA":
                if (acessoAtualizado.getDiaria() == null || acessoAtualizado.getDiaria().getId() == null) {
                    throw new DescricaoEmBrancoException("Para o tipo 'DIARIA', é necessário fornecer o ID da configuração de diária na atualização.");
                }
                Diaria diaria = diariaRepository.findById(acessoAtualizado.getDiaria().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Diária com ID " + acessoAtualizado.getDiaria().getId() + " não encontrada para atualização."));
                acessoExistente.setDiaria(diaria);
                break;
            case "MENSALISTA":
                if (acessoAtualizado.getMensalista() == null || acessoAtualizado.getMensalista().getId() == null) {
                    throw new DescricaoEmBrancoException("Para o tipo 'MENSALISTA', é necessário fornecer o ID da configuração de mensalista na atualização.");
                }
                Mensalista mensalista = mensalistaRepository.findById(acessoAtualizado.getMensalista().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException("Configuração de Mensalista com ID " + acessoAtualizado.getMensalista().getId() + " não encontrada para atualização."));
                acessoExistente.setMensalista(mensalista);
                break;
            default:
                throw new ValorAcessoInvalidoException("Tipo de acesso inválido na atualização: " + acessoExistente.getTipoAcesso());
        }

        return acessoRepository.save(acessoExistente);
    }

    @Transactional
    public void deletarAcesso(Long id) {
        if (!acessoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Acesso com ID " + id + " não encontrado para exclusão.");
        }
        acessoRepository.deleteById(id);
    }

    private BigDecimal calcularValorAcesso(Acesso acesso) {
        if (acesso.getEntrada() == null || acesso.getSaida() == null) {
            return BigDecimal.ZERO;
        }

        Duration duracao = Duration.between(acesso.getEntrada(), acesso.getSaida());
        long minutosTotais = duracao.toMinutes();

        if (minutosTotais < 0) {
            throw new ValorAcessoInvalidoException("Duração do acesso negativa. Hora de saída antes da entrada.");
        }

        switch (acesso.getTipoAcesso().toUpperCase()) {
            case "TEMPO":
                if (acesso.getTempo() == null) {
                    throw new ValorAcessoInvalidoException("Configuração de Tempo ausente para cálculo de acesso por Tempo.");
                }
                BigDecimal valorFracao = acesso.getTempo().getValorFracao();
                BigDecimal descontoPercentual = acesso.getTempo().getDesconto() != null ? acesso.getTempo().getDesconto() : BigDecimal.ZERO;

                BigDecimal totalMinutosBd = BigDecimal.valueOf(minutosTotais);
                BigDecimal quinzeMinutosBd = BigDecimal.valueOf(15);
                BigDecimal fracoesExatas = totalMinutosBd.divide(quinzeMinutosBd, 2, ROUNDING_MODE);
                BigDecimal fracoesArredondadas = fracoesExatas.setScale(0, RoundingMode.UP);

                BigDecimal valorBruto = fracoesArredondadas.multiply(valorFracao);

                BigDecimal fatorDesconto = BigDecimal.ONE.subtract(descontoPercentual.divide(CEM, SCALE, ROUNDING_MODE));
                
                return valorBruto.multiply(fatorDesconto).setScale(2, ROUNDING_MODE);

            case "DIARIA":
                if (acesso.getDiaria() == null) {
                    throw new ValorAcessoInvalidoException("Configuração de Diária ausente para cálculo de acesso por Diária.");
                }
                BigDecimal valorDiariaBase = acesso.getDiaria().getValor();

                if (acesso.getDiaria().getDiariaNoturna() != null) {
                    DiariaNoturna diariaNoturna = acesso.getDiaria().getDiariaNoturna();
                    BigDecimal adicionalNoturnoPercentual = diariaNoturna.getAdicionalNoturno() != null ? diariaNoturna.getAdicionalNoturno() : BigDecimal.ZERO;

                    BigDecimal fatorAdicional = BigDecimal.ONE.add(adicionalNoturnoPercentual.divide(CEM, SCALE, ROUNDING_MODE));
                    
                    return valorDiariaBase.multiply(fatorAdicional).setScale(2, ROUNDING_MODE);
                }
                return valorDiariaBase.setScale(2, ROUNDING_MODE);

            case "MENSALISTA":
                return BigDecimal.ZERO;

            default:
                throw new ValorAcessoInvalidoException("Tipo de acesso desconhecido para cálculo de valor: " + acesso.getTipoAcesso());
        }
    }
}