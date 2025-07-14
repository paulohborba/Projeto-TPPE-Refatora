package com.estacionamento.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.*;
import com.estacionamento.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AcessoService {

    private final AcessoRepository acessoRepository;
    private final EstacionamentoRepository estacionamentoRepository;
    private final VeiculoRepository veiculoRepository;
    private final TempoRepository tempoRepository;
    private final DiariaRepository diariaRepository;
    private final MensalistaRepository mensalistaRepository;

    public AcessoService(AcessoRepository acessoRepository, EstacionamentoRepository estacionamentoRepository,
                         VeiculoRepository veiculoRepository, TempoRepository tempoRepository,
                         DiariaRepository diariaRepository, MensalistaRepository mensalistaRepository) {
        this.acessoRepository = acessoRepository;
        this.estacionamentoRepository = estacionamentoRepository;
        this.veiculoRepository = veiculoRepository;
        this.tempoRepository = tempoRepository;
        this.diariaRepository = diariaRepository;
        this.mensalistaRepository = mensalistaRepository;
    }

    @Transactional
    public Acesso criarAcesso(Acesso acesso) {
        validarAcesso(acesso);

        Estacionamento estacionamento = estacionamentoRepository.findById(acesso.getEstacionamento().getId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        "Estacionamento com ID " + acesso.getEstacionamento().getId() + " não encontrado."
                ));
        acesso.setEstacionamento(estacionamento);

        Veiculo veiculo = veiculoRepository.findByPlaca(acesso.getVeiculo().getPlaca())
                .orElseGet(() -> veiculoRepository.save(acesso.getVeiculo()));
        acesso.setVeiculo(veiculo);

        if (acesso.getTipoAcesso() == null || acesso.getTipoAcesso().isEmpty()) {
            throw new DescricaoEmBrancoException("Tipo de acesso não pode estar em branco.");
        }

        switch (acesso.getTipoAcesso().toUpperCase()) {
            case "TEMPO":
                if (acesso.getTempo() == null || acesso.getTempo().getId() == null) {
                    throw new DescricaoEmBrancoException(
                        "Tempo associado ao acesso não pode ser nulo para tipo TEMPO."
                    );
                }
                Tempo tempo = tempoRepository.findById(acesso.getTempo().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                "Tempo com ID " + acesso.getTempo().getId() + " não encontrado."
                        ));
                acesso.setTempo(tempo);
                break;
            case "DIARIA":
                if (acesso.getDiaria() == null || acesso.getDiaria().getId() == null) {
                    throw new DescricaoEmBrancoException(
                        "Diaria associada ao acesso não pode ser nula para tipo DIARIA."
                    );
                }
                
                Diaria diariaDoBanco = diariaRepository.findById(acesso.getDiaria().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                "Diaria com ID " + acesso.getDiaria().getId() + " não encontrada."
                        ));

                if (acesso.getDiaria().getDiariaNoturna() != null) {
                    diariaDoBanco.setDiariaNoturna(acesso.getDiaria().getDiariaNoturna());
                }
                
                acesso.setDiaria(diariaDoBanco);
                break;
            case "MENSALISTA":
                if (acesso.getMensalista() == null || acesso.getMensalista().getId() == null) {
                    throw new DescricaoEmBrancoException(
                        "Mensalista associado ao acesso não pode ser nulo para tipo MENSALISTA."
                    );
                }
                Mensalista mensalista = mensalistaRepository.findById(acesso.getMensalista().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                "Mensalista com ID " + acesso.getMensalista().getId() + " não encontrado."
                        ));
                acesso.setMensalista(mensalista);
                break;
            default:
                throw new IllegalArgumentException("Tipo de acesso inválido: " + acesso.getTipoAcesso());
        }

        if (acesso.getDataFim() != null && acesso.getHoraFim() != null) {
            acesso.setValorCobrado(calcularValor(acesso));
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
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        "Acesso com ID " + id + " não encontrado para atualização."
                ));

        validarAcesso(acessoAtualizado);

        Estacionamento estacionamento = estacionamentoRepository.findById(acessoAtualizado.getEstacionamento().getId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        "Estacionamento com ID " + acessoAtualizado.getEstacionamento().getId() + " não encontrado."
                ));
        acessoExistente.setEstacionamento(estacionamento);

        Veiculo veiculo = veiculoRepository.findByPlaca(acessoAtualizado.getVeiculo().getPlaca())
                .orElseGet(() -> veiculoRepository.save(acessoAtualizado.getVeiculo()));
        acessoExistente.setVeiculo(veiculo);

        acessoExistente.setDataInicio(acessoAtualizado.getDataInicio());
        acessoExistente.setHoraInicio(acessoAtualizado.getHoraInicio());
        acessoExistente.setDataFim(acessoAtualizado.getDataFim());
        acessoExistente.setHoraFim(acessoAtualizado.getHoraFim());
        acessoExistente.setTipoAcesso(acessoAtualizado.getTipoAcesso());

        acessoExistente.setTempo(null);
        acessoExistente.setDiaria(null);
        acessoExistente.setMensalista(null);

        switch (acessoAtualizado.getTipoAcesso().toUpperCase()) {
            case "TEMPO":
                if (acessoAtualizado.getTempo() == null || acessoAtualizado.getTempo().getId() == null) {
                    throw new DescricaoEmBrancoException(
                        "Tempo associado ao acesso não pode ser nulo para tipo TEMPO."
                    );
                }
                Tempo tempo = tempoRepository.findById(acessoAtualizado.getTempo().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                "Tempo com ID " + acessoAtualizado.getTempo().getId() + " não encontrado."
                        ));
                acessoExistente.setTempo(tempo);
                break;
            case "DIARIA":
                if (acessoAtualizado.getDiaria() == null || acessoAtualizado.getDiaria().getId() == null) {
                    throw new DescricaoEmBrancoException(
                        "Diaria associada ao acesso não pode ser nula para tipo DIARIA."
                    );
                }
                Diaria diariaDoBancoAtualizado = diariaRepository.findById(acessoAtualizado.getDiaria().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                "Diaria com ID " + acessoAtualizado.getDiaria().getId() + " não encontrada."));

                if (acessoAtualizado.getDiaria().getDiariaNoturna() != null) {
                    diariaDoBancoAtualizado.setDiariaNoturna(acessoAtualizado.getDiaria().getDiariaNoturna());
                }
                
                acessoExistente.setDiaria(diariaDoBancoAtualizado);
                break;
            case "MENSALISTA":
                if (acessoAtualizado.getMensalista() == null || acessoAtualizado.getMensalista().getId() == null) {
                    throw new DescricaoEmBrancoException(
                        "Mensalista associado ao acesso não pode ser nulo para tipo MENSALISTA."
                    );
                }
                Mensalista mensalista = mensalistaRepository.findById(acessoAtualizado.getMensalista().getId())
                        .orElseThrow(() -> new ObjetoNaoEncontradoException(
                                "Mensalista com ID " + acessoAtualizado.getMensalista().getId() + " não encontrado."
                        ));
                acessoExistente.setMensalista(mensalista);
                break;
            default:
                throw new IllegalArgumentException("Tipo de acesso inválido: " + acessoAtualizado.getTipoAcesso());
        }

        if (acessoAtualizado.getDataFim() != null && acessoAtualizado.getHoraFim() != null) {
            acessoExistente.setValorCobrado(calcularValor(acessoExistente));
        } else {
            acessoExistente.setValorCobrado(null);
        }

        return acessoRepository.save(acessoExistente);
    }

    @Transactional
    public void deletarAcesso(Long id) {
        Acesso acesso = acessoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        "Acesso com ID " + id + " não encontrado para exclusão."
                ));
        acessoRepository.delete(acesso);
    }

    private void validarAcesso(Acesso acesso) {
        if (acesso.getEstacionamento() == null || acesso.getEstacionamento().getId() == null) {
            throw new DescricaoEmBrancoException("Estacionamento não pode ser nulo ou ter ID nulo.");
        }
        if (acesso.getVeiculo() == null || !StringUtils.hasText(acesso.getVeiculo().getPlaca())) {
            throw new DescricaoEmBrancoException("Veículo não pode ser nulo ou ter placa em branco.");
        }
        if (acesso.getDataInicio() == null || acesso.getHoraInicio() == null) {
            throw new DescricaoEmBrancoException("Data e hora de início não podem ser nulas.");
        }

        if (acesso.getDataFim() != null && acesso.getHoraFim() != null) {
            LocalDateTime entrada = LocalDateTime.of(acesso.getDataInicio(), acesso.getHoraInicio());
            LocalDateTime saida = LocalDateTime.of(acesso.getDataFim(), acesso.getHoraFim());
            
            if (saida.isBefore(entrada)) {
                throw new IllegalArgumentException("Data/hora de saída não pode ser anterior à data/hora de entrada.");
            }
        }
    }

    private LocalDateTime getEntrada(Acesso acesso) {
        return LocalDateTime.of(acesso.getDataInicio(), acesso.getHoraInicio());
    }

    private LocalDateTime getSaida(Acesso acesso) {
        if (acesso.getDataFim() == null || acesso.getHoraFim() == null) {
            return null;
        }
        return LocalDateTime.of(acesso.getDataFim(), acesso.getHoraFim());
    }

    @SuppressWarnings({ "deprecation", "null" })
    public BigDecimal calcularValor(Acesso acesso) {
        LocalDateTime saida = getSaida(acesso);
        if (saida == null) {
            return BigDecimal.ZERO;
        }

        LocalDateTime entrada = getEntrada(acesso);
        Duration duracao = Duration.between(entrada, saida);
        long minutos = duracao.toMinutes();

        switch (acesso.getTipoAcesso().toUpperCase()) {
            case "TEMPO":
                if (acesso.getTempo() == null) {
                    throw new IllegalStateException("Configuração de Tempo não encontrada para este acesso.");
                }
                long duracaoFracaoMinutos = (
                    acesso.getTempo().getDuracao().getHour() * 60L) + acesso.getTempo().getDuracao().getMinute();
                if (duracaoFracaoMinutos <= 0) {
                    throw new IllegalStateException("Duração da fração de tempo deve ser maior que zero.");
                }

                BigDecimal valorTotalTempo = BigDecimal.ZERO;
                if (minutos > 0) {
                    long numFracoes = (long) Math.ceil((double) minutos / duracaoFracaoMinutos);
                    valorTotalTempo = acesso.getTempo().getValorFracao().multiply(BigDecimal.valueOf(numFracoes));
                }

                if (acesso.getTempo().getDesconto() !=
                null && acesso.getTempo().getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal desconto =
                    valorTotalTempo.multiply(
                        acesso.getTempo().getDesconto().divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP)
                    );
                    valorTotalTempo = valorTotalTempo.subtract(desconto);
                }
                return valorTotalTempo.setScale(2, BigDecimal.ROUND_HALF_UP);

            case "DIARIA":
                if (acesso.getDiaria() == null) {
                    throw new IllegalStateException("Configuração de Diária não encontrada para este acesso.");
                }

                BigDecimal valorDiaria = acesso.getDiaria().getValor();
                if (valorDiaria == null) {
                    throw new IllegalStateException("Valor da Diária não pode ser nulo.");
                }

                DiariaNoturna diariaNoturna = acesso.getDiaria().getDiariaNoturna();
                if (diariaNoturna != null && diariaNoturna.getAdicionalNoturno() != null) {
                    if ((diariaNoturna.getHoraInicio().isBefore(diariaNoturna.getHoraFim()) &&
                         saida.toLocalTime().isAfter(diariaNoturna.getHoraInicio()) &&
                         saida.toLocalTime().isBefore(diariaNoturna.getHoraFim())) ||
                        (diariaNoturna.getHoraInicio().isAfter(diariaNoturna.getHoraFim()) &&
                         (saida.toLocalTime().isAfter(diariaNoturna.getHoraInicio()) ||
                          saida.toLocalTime().isBefore(diariaNoturna.getHoraFim())))
                    ) {
                        return valorDiaria.add(diariaNoturna.getAdicionalNoturno()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                return valorDiaria.setScale(2, BigDecimal.ROUND_HALF_UP);

            case "MENSALISTA":
                return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);

            default:
                return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }
}