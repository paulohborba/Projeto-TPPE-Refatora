package com.estacionamento_tppe.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Diaria;
import com.estacionamento.model.DiariaNoturna;
import com.estacionamento.repository.DiariaNoturnaRepository;
import com.estacionamento.service.DiariaNoturnaService;

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
class DiariaNoturnaServiceTest {

    @Mock
    private DiariaNoturnaRepository diariaNoturnaRepository;

    @InjectMocks
    private DiariaNoturnaService diariaNoturnaService;

    private DiariaNoturna diariaNoturnaValida;
    private Diaria diariaAssociada;

    @BeforeEach
    void setUp() {
        diariaAssociada = new Diaria();
        diariaAssociada.setId(1L);
        diariaAssociada.setValor(new BigDecimal("50.00"));

        diariaNoturnaValida = new DiariaNoturna();
        diariaNoturnaValida.setId(1L);
        diariaNoturnaValida.setHoraInicio(LocalTime.of(22, 0));
        diariaNoturnaValida.setHoraFim(LocalTime.of(6, 0));
        diariaNoturnaValida.setAdicionalNoturno(new BigDecimal("10.00"));
        diariaNoturnaValida.setDiaria(diariaAssociada);

        reset(diariaNoturnaRepository);
    }

    @Test
    @DisplayName("Deve retornar DiariaNoturna por ID quando encontrada")
    void deveRetornarDiariaNoturnaPorIdQuandoEncontrada() {
        when(diariaNoturnaRepository.findById(1L)).thenReturn(Optional.of(diariaNoturnaValida));

        DiariaNoturna encontrada = diariaNoturnaService.buscarDiariaNoturnaPorId(1L);

        assertNotNull(encontrada);
        assertEquals(1L, encontrada.getId());
        assertEquals(LocalTime.of(22, 0), encontrada.getHoraInicio());
        verify(diariaNoturnaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar DiariaNoturna por ID inexistente")
    void deveLancarExcecaoQuandoBuscarDiariaNoturnaPorIdInexistente() {
        when(diariaNoturnaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> diariaNoturnaService.buscarDiariaNoturnaPorId(99L));
        verify(diariaNoturnaRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todas as DiariasNoturnas")
    void deveListarTodasDiariasNoturnas() {
        DiariaNoturna outraDiariaNoturna = new DiariaNoturna();
        outraDiariaNoturna.setId(2L);
        outraDiariaNoturna.setHoraInicio(LocalTime.of(23, 0));
        outraDiariaNoturna.setHoraFim(LocalTime.of(7, 0));
        outraDiariaNoturna.setAdicionalNoturno(new BigDecimal("12.50"));
        outraDiariaNoturna.setDiaria(new Diaria(2L, new BigDecimal("60.00"), "Noturna Teste", "Desc", null));

        List<DiariaNoturna> diariasNoturnas = Arrays.asList(diariaNoturnaValida, outraDiariaNoturna);
        when(diariaNoturnaRepository.findAll()).thenReturn(diariasNoturnas);

        List<DiariaNoturna> lista = diariaNoturnaService.listarTodasDiariasNoturnas();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(diariaNoturnaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar uma DiariaNoturna existente com sucesso")
    void deveAtualizarDiariaNoturnaExistente() {
        DiariaNoturna atualizacaoPayload = new DiariaNoturna();
        atualizacaoPayload.setHoraInicio(LocalTime.of(21, 30));
        atualizacaoPayload.setHoraFim(LocalTime.of(6, 30));
        atualizacaoPayload.setAdicionalNoturno(new BigDecimal("18.00"));

        when(diariaNoturnaRepository.findById(1L)).thenReturn(Optional.of(diariaNoturnaValida));
        when(diariaNoturnaRepository.save(any(DiariaNoturna.class))).thenReturn(diariaNoturnaValida);

        DiariaNoturna atualizada = diariaNoturnaService.atualizarDiariaNoturna(1L, atualizacaoPayload);

        assertNotNull(atualizada);
        assertEquals(LocalTime.of(21, 30), atualizada.getHoraInicio());
        assertEquals(LocalTime.of(6, 30), atualizada.getHoraFim());
        assertEquals(new BigDecimal("18.00"), atualizada.getAdicionalNoturno());
        verify(diariaNoturnaRepository, times(1)).findById(1L);
        verify(diariaNoturnaRepository, times(1)).save(any(DiariaNoturna.class));
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar DiariaNoturna inexistente")
    void deveLancarExcecaoAoAtualizarDiariaNoturnaInexistente() {
        DiariaNoturna atualizacaoPayload = new DiariaNoturna();
        atualizacaoPayload.setHoraInicio(LocalTime.of(1, 0));
        atualizacaoPayload.setHoraFim(LocalTime.of(2, 0));
        atualizacaoPayload.setAdicionalNoturno(new BigDecimal("5.00"));

        when(diariaNoturnaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> 
        diariaNoturnaService.atualizarDiariaNoturna(99L, atualizacaoPayload));
        verify(diariaNoturnaRepository, never()).save(any(DiariaNoturna.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException se horaInicio for nula na atualização")
    void deveLancarExcecaoSeHoraInicioNulaNaAtualizacao() {
        DiariaNoturna atualizacaoPayload = new DiariaNoturna();
        atualizacaoPayload.setHoraInicio(null);
        atualizacaoPayload.setHoraFim(LocalTime.of(6, 0));
        atualizacaoPayload.setAdicionalNoturno(new BigDecimal("10.00"));

        when(diariaNoturnaRepository.findById(1L)).thenReturn(Optional.of(diariaNoturnaValida));
        assertThrows(DescricaoEmBrancoException.class, () -> 
        diariaNoturnaService.atualizarDiariaNoturna(1L, atualizacaoPayload));
        verify(diariaNoturnaRepository, never()).save(any(DiariaNoturna.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se adicionalNoturno for negativo na atualização")
    void deveLancarExcecaoSeAdicionalNoturnoNegativoNaAtualizacao() {
        DiariaNoturna atualizacaoPayload = new DiariaNoturna();
        atualizacaoPayload.setHoraInicio(LocalTime.of(22, 0));
        atualizacaoPayload.setHoraFim(LocalTime.of(6, 0));
        atualizacaoPayload.setAdicionalNoturno(new BigDecimal("-5.00"));

        when(diariaNoturnaRepository.findById(1L)).thenReturn(Optional.of(diariaNoturnaValida));

        assertThrows(IllegalArgumentException.class, () -> 
        diariaNoturnaService.atualizarDiariaNoturna(1L, atualizacaoPayload));
        verify(diariaNoturnaRepository, never()).save(any(DiariaNoturna.class));
    }

    @Test
    @DisplayName("Deve deletar uma DiariaNoturna existente com sucesso")
    void deveDeletarDiariaNoturnaExistente() {
        when(diariaNoturnaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(diariaNoturnaRepository).deleteById(1L);

        assertDoesNotThrow(() -> diariaNoturnaService.deletarDiariaNoturna(1L));

        verify(diariaNoturnaRepository, times(1)).existsById(1L);
        verify(diariaNoturnaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar DiariaNoturna inexistente")
    void deveLancarExcecaoAoDeletarDiariaNoturnaInexistente() {
        when(diariaNoturnaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ObjetoNaoEncontradoException.class, () -> diariaNoturnaService.deletarDiariaNoturna(99L));
        verify(diariaNoturnaRepository, times(1)).existsById(99L);
        verify(diariaNoturnaRepository, never()).deleteById(anyLong());
    }
}