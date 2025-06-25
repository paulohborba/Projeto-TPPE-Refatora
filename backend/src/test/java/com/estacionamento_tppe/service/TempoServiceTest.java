package com.estacionamento_tppe.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Tempo;
import com.estacionamento.repository.TempoRepository;
import com.estacionamento.service.TempoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TempoServiceTest {

    @Mock
    private TempoRepository tempoRepository;

    @InjectMocks
    private TempoService tempoService;

    private Tempo tempoValido;

    @BeforeEach
    void setUp() {
        tempoValido = new Tempo();
        tempoValido.setId(1L);
        tempoValido.setDuracao(LocalTime.of(0, 15, 0));
        tempoValido.setValorFracao(new BigDecimal("5.00"));
        tempoValido.setDesconto(new BigDecimal("0.00"));

        reset(tempoRepository);
    }

    @Test
    @DisplayName("Deve criar uma configuração de tempo válida com sucesso")
    void deveCriarTempoValido() {
        when(tempoRepository.save(any(Tempo.class))).thenReturn(tempoValido);

        Tempo salvo = tempoService.criarTempo(tempoValido);

        assertNotNull(salvo);
        assertEquals(new BigDecimal("5.00"), salvo.getValorFracao());
        verify(tempoRepository, times(1)).save(any(Tempo.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar criar tempo com valorFracao nulo")
    void deveLancarExcecaoQuandoCriarTempoComValorFracaoNulo() {
        tempoValido.setValorFracao(null);
        assertThrows(DescricaoEmBrancoException.class, () -> tempoService.criarTempo(tempoValido));
        verify(tempoRepository, never()).save(any(Tempo.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar criar tempo com valorFracao zero ou negativo")
    void deveLancarExcecaoQuandoCriarTempoComValorFracaoZeroOuNegativo() {
        tempoValido.setValorFracao(BigDecimal.ZERO);
        assertThrows(DescricaoEmBrancoException.class, () -> tempoService.criarTempo(tempoValido));
        tempoValido.setValorFracao(new BigDecimal("-1.00"));
        assertThrows(DescricaoEmBrancoException.class, () -> tempoService.criarTempo(tempoValido));
        verify(tempoRepository, never()).save(any(Tempo.class));
    }

    @Test
    @DisplayName("Deve retornar tempo por ID quando encontrado")
    void deveRetornarTempoPorIdQuandoEncontrado() {
        when(tempoRepository.findById(1L)).thenReturn(Optional.of(tempoValido));

        Tempo encontrado = tempoService.buscarTempoPorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        verify(tempoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar tempo por ID inexistente")
    void deveLancarExcecaoQuandoBuscarTempoPorIdInexistente() {
        when(tempoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> tempoService.buscarTempoPorId(99L));
        verify(tempoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todas as configurações de tempo")
    void deveListarTodosTempos() {
        List<Tempo> tempos = Arrays.asList(tempoValido, new Tempo());
        when(tempoRepository.findAll()).thenReturn(tempos);

        List<Tempo> lista = tempoService.listarTodosTempos();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(tempoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar uma configuração de tempo existente com sucesso")
    void deveAtualizarTempoExistente() {
        Tempo atualizacaoPayload = new Tempo();
        atualizacaoPayload.setDuracao(LocalTime.of(0, 30, 0));
        atualizacaoPayload.setValorFracao(new BigDecimal("8.00"));
        atualizacaoPayload.setDesconto(new BigDecimal("5.00"));

        when(tempoRepository.findById(1L)).thenReturn(Optional.of(tempoValido));
        when(tempoRepository.save(any(Tempo.class))).thenReturn(tempoValido);

        Tempo atualizado = tempoService.atualizarTempo(1L, atualizacaoPayload);

        assertNotNull(atualizado);
        assertEquals(LocalTime.of(0, 30, 0), atualizado.getDuracao());
        assertEquals(new BigDecimal("8.00"), atualizado.getValorFracao());
        assertEquals(new BigDecimal("5.00"), atualizado.getDesconto());
        verify(tempoRepository, times(1)).findById(1L);
        verify(tempoRepository, times(1)).save(tempoValido);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar tempo inexistente")
    void deveLancarExcecaoAoAtualizarTempoInexistente() {
        Tempo atualizacaoPayload = new Tempo();
        atualizacaoPayload.setValorFracao(new BigDecimal("10.00"));

        when(tempoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> tempoService.atualizarTempo(99L, atualizacaoPayload));
        verify(tempoRepository, never()).save(any(Tempo.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException se valorFracao for nulo na atualização")
    void deveLancarExcecaoSeValorFracaoNuloNaAtualizacao() {
        Tempo atualizacaoPayload = new Tempo();
        atualizacaoPayload.setValorFracao(null);
        atualizacaoPayload.setDuracao(LocalTime.of(0, 15, 0));

        when(tempoRepository.findById(1L)).thenReturn(Optional.of(tempoValido));

        assertThrows(DescricaoEmBrancoException.class, () -> tempoService.atualizarTempo(1L, atualizacaoPayload));
        verify(tempoRepository, never()).save(any(Tempo.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException se valorFracao for zero ou negativo na atualização")
    void deveLancarExcecaoSeValorFracaoInvalidoNaAtualizacao() {
        Tempo atualizacaoPayload = new Tempo();
        atualizacaoPayload.setValorFracao(BigDecimal.ZERO);
        atualizacaoPayload.setDuracao(LocalTime.of(0, 15, 0));

        when(tempoRepository.findById(1L)).thenReturn(Optional.of(tempoValido));

        assertThrows(DescricaoEmBrancoException.class, () -> tempoService.atualizarTempo(1L, atualizacaoPayload));
        verify(tempoRepository, never()).save(any(Tempo.class));
    }

    @Test
    @DisplayName("Deve deletar uma configuração de tempo existente com sucesso")
    void deveDeletarTempoExistente() {
        when(tempoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tempoRepository).deleteById(1L);

        assertDoesNotThrow(() -> tempoService.deletarTempo(1L));

        verify(tempoRepository, times(1)).existsById(1L);
        verify(tempoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar tempo inexistente")
    void deveLancarExcecaoAoDeletarTempoInexistente() {
        when(tempoRepository.existsById(99L)).thenReturn(false);

        assertThrows(ObjetoNaoEncontradoException.class, () -> tempoService.deletarTempo(99L));
        verify(tempoRepository, times(1)).existsById(99L);
        verify(tempoRepository, never()).deleteById(anyLong());
    }
}