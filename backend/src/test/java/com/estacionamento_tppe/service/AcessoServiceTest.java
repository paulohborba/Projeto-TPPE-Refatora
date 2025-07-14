package com.estacionamento_tppe.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.*;
import com.estacionamento.repository.*;
import com.estacionamento.service.AcessoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcessoServiceTest {

    @Mock
    private AcessoRepository acessoRepository;
    @Mock
    private EstacionamentoRepository estacionamentoRepository;
    @Mock
    private VeiculoRepository veiculoRepository;
    @Mock
    private TempoRepository tempoRepository;
    @Mock
    private DiariaRepository diariaRepository;
    @Mock
    private MensalistaRepository mensalistaRepository;

    @InjectMocks
    private AcessoService acessoService;

    private Estacionamento estacionamentoPadrao;
    private Veiculo veiculoPadrao;
    private Tempo tempoPadrao;
    private Diaria diariaPadrao;
    private DiariaNoturna diariaNoturnaPadrao;
    private Mensalista mensalistaPadrao;
    private Acesso acessoValido;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        estacionamentoPadrao = new Estacionamento(1L, "Estacionamento Central", "Rua A, 123", 100,
                LocalTime.of(8, 0), LocalTime.of(22, 0), new HashSet<>());
        veiculoPadrao = new Veiculo(1L, "ABC1234", "Fiat", "Uno", "Preto");
        tempoPadrao = new Tempo(1L, LocalTime.of(0, 15), BigDecimal.valueOf(10.00), BigDecimal.valueOf(10.0));

        diariaNoturnaPadrao = new DiariaNoturna(
            2L, LocalTime.of(22, 0), LocalTime.of(6, 0), BigDecimal.valueOf(5.00), null
        );
        diariaPadrao = new Diaria(
            2L, BigDecimal.valueOf(50.00), "DIARIA_COMUM", "Diária normal", diariaNoturnaPadrao
        );
        diariaNoturnaPadrao.setDiaria(diariaPadrao);

        mensalistaPadrao = new Mensalista(3L, BigDecimal.valueOf(300.00), 1, "Plano Mensal");

        acessoValido = new Acesso();
        acessoValido.setId(1L);
        acessoValido.setEstacionamento(estacionamentoPadrao);
        acessoValido.setVeiculo(veiculoPadrao);
        acessoValido.setDataInicio(LocalDate.of(2024, 7, 1));
        acessoValido.setHoraInicio(LocalTime.of(9, 7, 1));
        acessoValido.setTipoAcesso("TEMPO");
        acessoValido.setTempo(tempoPadrao);

        reset(
            acessoRepository, estacionamentoRepository, veiculoRepository, 
            tempoRepository, diariaRepository, mensalistaRepository
        );
    }

    @Test
    @DisplayName("Deve criar um acesso do tipo TEMPO com sucesso")
    void deveCriarAcessoTempoComSucesso() {
        lenient().when(estacionamentoRepository.findById(estacionamentoPadrao.getId())).thenReturn(Optional.of(
            estacionamentoPadrao)
        );
        lenient().when(veiculoRepository.findByPlaca(veiculoPadrao.getPlaca())).thenReturn(Optional.of(veiculoPadrao));
        lenient().when(tempoRepository.findById(tempoPadrao.getId())).thenReturn(Optional.of(tempoPadrao));
        when(acessoRepository.save(any(Acesso.class))).thenReturn(acessoValido);

        Acesso salvo = acessoService.criarAcesso(acessoValido);

        assertNotNull(salvo);
        assertEquals("TEMPO", salvo.getTipoAcesso());
        verify(acessoRepository, times(1)).save(acessoValido);
    }

    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("Deve criar um acesso do tipo DIARIA com sucesso e calcular valor se saída definida")
    void deveCriarAcessoDiariaComSucessoECalcularValorComSaida() {
        acessoValido.setTipoAcesso("DIARIA");
        acessoValido.setDiaria(diariaPadrao);
        acessoValido.setDataInicio(LocalDate.of(2024, 7, 1));
        acessoValido.setHoraInicio(LocalTime.of(10, 0, 1));
        acessoValido.setDataFim(LocalDate.of(2024, 7, 1));
        acessoValido.setHoraFim(LocalTime.of(23, 0, 1));

        lenient().when(estacionamentoRepository.findById(estacionamentoPadrao.getId())).thenReturn(Optional.of(
            estacionamentoPadrao)
        );
        lenient().when(veiculoRepository.findByPlaca(veiculoPadrao.getPlaca())).thenReturn(Optional.of(veiculoPadrao));
        lenient().when(diariaRepository.findById(diariaPadrao.getId())).thenReturn(Optional.of(diariaPadrao));
        when(acessoRepository.save(any(Acesso.class))).thenAnswer(invocation -> {
            Acesso a = invocation.getArgument(0);
            a.setId(1L);
            a.setValorCobrado(acessoService.calcularValor(a));
            return a;
        });

        Acesso salvo = acessoService.criarAcesso(acessoValido);

        assertNotNull(salvo);
        assertEquals("DIARIA", salvo.getTipoAcesso());
        assertEquals(BigDecimal.valueOf(55.00).setScale(2, BigDecimal.ROUND_HALF_UP), salvo.getValorCobrado());
        verify(acessoRepository, times(1)).save(any(Acesso.class));
    }

    @Test
    @DisplayName("Deve criar um acesso do tipo MENSALISTA com sucesso")
    void deveCriarAcessoMensalistaComSucesso() {
        acessoValido.setTipoAcesso("MENSALISTA");
        acessoValido.setMensalista(mensalistaPadrao);
        acessoValido.setDataFim(null);
        acessoValido.setHoraFim(null);

        lenient().when(estacionamentoRepository.findById(estacionamentoPadrao.getId())).thenReturn(Optional.of(
            estacionamentoPadrao)
        );
        lenient().when(veiculoRepository.findByPlaca(veiculoPadrao.getPlaca())).thenReturn(Optional.of(veiculoPadrao));
        lenient().when(mensalistaRepository.findById(mensalistaPadrao.getId())).thenReturn(Optional.of(
            mensalistaPadrao)
        );
        when(acessoRepository.save(any(Acesso.class))).thenReturn(acessoValido);

        Acesso salvo = acessoService.criarAcesso(acessoValido);

        assertNotNull(salvo);
        assertEquals("MENSALISTA", salvo.getTipoAcesso());
        assertNull(salvo.getValorCobrado());
        verify(acessoRepository, times(1)).save(acessoValido);
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException se estacionamento for nulo")
    void deveLancarExcecaoSeEstacionamentoForNulo() {
        acessoValido.setEstacionamento(null);
        assertThrows(DescricaoEmBrancoException.class, () -> acessoService.criarAcesso(acessoValido));
        verify(acessoRepository, never()).save(any(Acesso.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException se placa do veículo for em branco")
    void deveLancarExcecaoSePlacaVeiculoEmBranco() {
        acessoValido.getVeiculo().setPlaca("");
        assertThrows(DescricaoEmBrancoException.class, () -> acessoService.criarAcesso(acessoValido));
        verify(acessoRepository, never()).save(any(Acesso.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException se hora de entrada for nula")
    void deveLancarExcecaoSeEntradaNula() {
        acessoValido.setDataInicio(null);
        acessoValido.setHoraInicio(null);
        assertThrows(DescricaoEmBrancoException.class, () -> acessoService.criarAcesso(acessoValido));
        verify(acessoRepository, never()).save(any(Acesso.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se hora de saída for anterior à entrada")
    void deveLancarExcecaoSeSaidaAnteriorAEntrada() {
        acessoValido.setDataInicio(LocalDate.of(2024, 7, 1));
        acessoValido.setHoraInicio(LocalTime.of(10, 0, 0));
        acessoValido.setDataFim(LocalDate.of(2024, 7, 1));
        acessoValido.setHoraFim(LocalTime.of(9, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> acessoService.criarAcesso(acessoValido));
        verify(acessoRepository, never()).save(any(Acesso.class));
    }

    @Test
    @DisplayName("Deve retornar acesso por ID quando encontrado")
    void deveRetornarAcessoPorIdQuandoEncontrado() {
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(acessoValido));

        Acesso encontrado = acessoService.buscarAcessoPorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        verify(acessoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar acesso por ID inexistente")
    void deveLancarExcecaoAoBuscarAcessoPorIdInexistente() {
        when(acessoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> acessoService.buscarAcessoPorId(99L));
        verify(acessoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todos os acessos")
    void deveListarTodosAcessos() {
        List<Acesso> acessos = Arrays.asList(acessoValido, new Acesso());
        when(acessoRepository.findAll()).thenReturn(acessos);

        List<Acesso> lista = acessoService.listarTodosAcessos();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(acessoRepository, times(1)).findAll();
    }

    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("Deve atualizar um acesso existente com sucesso")
    void deveAtualizarAcessoExistente() {
        Acesso acessoExistente = new Acesso();
        acessoExistente.setId(1L);
        acessoExistente.setEstacionamento(estacionamentoPadrao);
        acessoExistente.setVeiculo(veiculoPadrao);
        acessoExistente.setDataInicio(LocalDate.of(2024, 7, 1));
        acessoExistente.setHoraInicio(LocalTime.of(9, 0, 0));
        acessoExistente.setTipoAcesso("TEMPO");
        acessoExistente.setTempo(tempoPadrao);

        Acesso acessoAtualizado = new Acesso();
        acessoAtualizado.setEstacionamento(estacionamentoPadrao);
        acessoAtualizado.setVeiculo(new Veiculo(2L, "DEF5678", "VW", "Gol", "Azul"));
        acessoAtualizado.setDataInicio(LocalDate.of(2024, 7, 1));
        acessoAtualizado.setHoraInicio(LocalTime.of(9, 30, 0));
        acessoAtualizado.setDataFim(LocalDate.of(2024, 7, 1));
        acessoAtualizado.setHoraFim(LocalTime.of(10, 0, 0));
        acessoAtualizado.setTipoAcesso("TEMPO");
        acessoAtualizado.setTempo(tempoPadrao);

        lenient().when(acessoRepository.findById(1L)).thenReturn(Optional.of(acessoExistente));
        lenient().when(estacionamentoRepository.findById(estacionamentoPadrao.getId())).thenReturn(Optional.of(
            estacionamentoPadrao)
        );
        lenient().when(veiculoRepository.findByPlaca("DEF5678")).thenReturn(Optional.of(acessoAtualizado.getVeiculo()));
        lenient().when(tempoRepository.findById(tempoPadrao.getId())).thenReturn(Optional.of(tempoPadrao));
        when(acessoRepository.save(any(Acesso.class))).thenAnswer(i -> i.getArguments()[0]);


        Acesso atualizado = acessoService.atualizarAcesso(1L, acessoAtualizado);

        assertNotNull(atualizado);
        assertEquals("DEF5678", atualizado.getVeiculo().getPlaca());
        assertEquals(LocalDate.of(2024, 7, 1), atualizado.getDataInicio());
        assertEquals(LocalTime.of(9, 30, 0), atualizado.getHoraInicio());
        assertEquals(LocalDate.of(2024, 7, 1), atualizado.getDataFim());
        assertEquals(LocalTime.of(10, 0, 0), atualizado.getHoraFim());
        assertEquals(BigDecimal.valueOf(18.00).setScale(2, BigDecimal.ROUND_HALF_UP), atualizado.getValorCobrado());
        verify(acessoRepository, times(1)).findById(1L);
        verify(acessoRepository, times(1)).save(acessoExistente);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar acesso inexistente")
    void deveLancarExcecaoAoAtualizarAcessoInexistente() {
        Acesso acessoAtualizado = new Acesso();
        acessoAtualizado.setEstacionamento(estacionamentoPadrao);
        acessoAtualizado.setVeiculo(veiculoPadrao);
        acessoAtualizado.setDataInicio(LocalDate.now());
        acessoAtualizado.setHoraInicio(LocalTime.now());
        acessoAtualizado.setTipoAcesso("TEMPO");
        acessoAtualizado.setTempo(tempoPadrao);

        when(acessoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> acessoService.atualizarAcesso(99L, acessoAtualizado));
        verify(acessoRepository, never()).save(any(Acesso.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se hora de saída for anterior à entrada na atualização")
    void deveLancarExcecaoSeSaidaAnteriorAEntradaNaAtualizacao() {
        Acesso acessoExistente = new Acesso();
        acessoExistente.setId(1L);
        acessoExistente.setEstacionamento(estacionamentoPadrao);
        acessoExistente.setVeiculo(veiculoPadrao);
        acessoExistente.setDataInicio(LocalDate.of(2024, 7, 1));
        acessoExistente.setHoraInicio(LocalTime.of(9, 0, 0));
        acessoExistente.setTipoAcesso("TEMPO");
        acessoExistente.setTempo(tempoPadrao);

        Acesso acessoAtualizado = new Acesso();
        acessoAtualizado.setEstacionamento(estacionamentoPadrao);
acessoAtualizado.setVeiculo(veiculoPadrao);
        acessoAtualizado.setDataInicio(LocalDate.of(2024, 7, 1));
        acessoAtualizado.setHoraInicio(LocalTime.of(10, 0, 0));
        acessoAtualizado.setDataFim(LocalDate.of(2024, 7, 1));
        acessoAtualizado.setHoraFim(LocalTime.of(9, 0, 0));
        acessoAtualizado.setTipoAcesso("TEMPO");
        acessoAtualizado.setTempo(tempoPadrao);

        lenient().when(acessoRepository.findById(1L)).thenReturn(Optional.of(acessoExistente));
        lenient().when(estacionamentoRepository.findById(estacionamentoPadrao.getId())).thenReturn(Optional.of(
            estacionamentoPadrao)
        );
        lenient().when(veiculoRepository.findByPlaca(veiculoPadrao.getPlaca())).thenReturn(Optional.of(veiculoPadrao));
        lenient().when(tempoRepository.findById(tempoPadrao.getId())).thenReturn(Optional.of(tempoPadrao));

        assertThrows(IllegalArgumentException.class, () -> acessoService.atualizarAcesso(1L, acessoAtualizado));
        verify(acessoRepository, never()).save(any(Acesso.class));
    }

    @Test
    @DisplayName("Deve deletar um acesso existente com sucesso")
    void deveDeletarAcessoExistente() {
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(acessoValido));
        doNothing().when(acessoRepository).delete(acessoValido);

        assertDoesNotThrow(() -> acessoService.deletarAcesso(1L));

        verify(acessoRepository, times(1)).findById(1L);
        verify(acessoRepository, times(1)).delete(acessoValido);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar acesso inexistente")
    void deveLancarExcecaoAoDeletarAcessoInexistente() {
        when(acessoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> acessoService.deletarAcesso(99L));
        verify(acessoRepository, never()).delete(any(Acesso.class));
    }
}
