package com.estacionamento_tppe.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Diaria;
import com.estacionamento.model.DiariaNoturna;
import com.estacionamento.repository.DiariaNoturnaRepository;
import com.estacionamento.repository.DiariaRepository;
import com.estacionamento.service.DiariaService;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiariaServiceTest {

    @Mock
    private DiariaRepository diariaRepository;
    @Mock
    private DiariaNoturnaRepository diariaNoturnaRepository;

    @InjectMocks
    private DiariaService diariaService;

    private Diaria diariaValida;
    private DiariaNoturna diariaNoturnaValida;

    @BeforeEach
    void setUp() {
        diariaNoturnaValida = new DiariaNoturna(null, LocalTime.of(22, 0), LocalTime.of(6, 0), BigDecimal.valueOf(5.00), null);
        diariaValida = new Diaria(1L, BigDecimal.valueOf(50.00), "DIARIA_COMUM", "Diária normal", diariaNoturnaValida);
        diariaNoturnaValida.setDiaria(diariaValida);
    }

    @Test
    @DisplayName("Deve criar uma diária válida com sucesso")
    void deveCriarDiariaValida() {
        when(diariaRepository.save(any(Diaria.class))).thenAnswer(invocation -> {
            Diaria d = invocation.getArgument(0);
            d.setId(1L);
            if (d.getDiariaNoturna() != null) {
                d.getDiariaNoturna().setId(d.getId());
                d.getDiariaNoturna().setDiaria(d);
            }
            return d;
        });
        when(diariaNoturnaRepository.save(any(DiariaNoturna.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Diaria salvo = diariaService.criarDiaria(diariaValida);

        assertNotNull(salvo);
        assertEquals(BigDecimal.valueOf(50.00), salvo.getValor());
        assertEquals("DIARIA_COMUM", salvo.getTipo());
        assertNotNull(salvo.getDiariaNoturna());
        assertEquals(LocalTime.of(22, 0), salvo.getDiariaNoturna().getHoraInicio());
        verify(diariaRepository, times(1)).save(diariaValida);
        verify(diariaNoturnaRepository, times(1)).save(diariaNoturnaValida);
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao criar diária com valor nulo")
    void deveLancarExcecaoQuandoCriarDiariaComValorNulo() {
        diariaValida.setValor(null);
        assertThrows(DescricaoEmBrancoException.class, () -> diariaService.criarDiaria(diariaValida));
        verify(diariaRepository, never()).save(any(Diaria.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao criar diária com tipo em branco")
    void deveLancarExcecaoQuandoCriarDiariaComTipoEmBranco() {
        diariaValida.setTipo("");
        assertThrows(DescricaoEmBrancoException.class, () -> diariaService.criarDiaria(diariaValida));
        verify(diariaRepository, never()).save(any(Diaria.class));
    }

    @Test
    @DisplayName("Deve retornar diária por ID quando encontrado")
    void deveRetornarDiariaPorIdQuandoEncontrado() {
        when(diariaRepository.findById(1L)).thenReturn(Optional.of(diariaValida));

        Diaria encontrado = diariaService.buscarDiariaPorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        verify(diariaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar diária por ID inexistente")
    void deveLancarExcecaoQuandoBuscarDiariaPorIdInexistente() {
        when(diariaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> diariaService.buscarDiariaPorId(99L));
        verify(diariaRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todas as diárias")
    void deveListarTodasDiarias() {
        List<Diaria> diarias = Arrays.asList(diariaValida, new Diaria());
        when(diariaRepository.findAll()).thenReturn(diarias);

        List<Diaria> lista = diariaService.listarTodasDiarias();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(diariaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar uma diária existente com sucesso")
    void deveAtualizarDiariaExistente() {
        Diaria diariaExistente = new Diaria(1L, BigDecimal.valueOf(60.00), "DIARIA_NOVA", "Descrição antiga", null);
        Diaria diariaAtualizada = new Diaria(null, BigDecimal.valueOf(70.00), "DIARIA_ATUALIZADA", "Nova descrição", null);

        when(diariaRepository.findById(1L)).thenReturn(Optional.of(diariaExistente));
        when(diariaRepository.save(any(Diaria.class))).thenReturn(diariaAtualizada);

        Diaria atualizada = diariaService.atualizarDiaria(1L, diariaAtualizada);

        assertNotNull(atualizada);
        assertEquals(BigDecimal.valueOf(70.00), atualizada.getValor());
        assertEquals("DIARIA_ATUALIZADA", atualizada.getTipo());
        assertEquals("Nova descrição", atualizada.getDescricao());
        verify(diariaRepository, times(1)).findById(1L);
        verify(diariaRepository, times(1)).save(diariaExistente);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar diária inexistente")
    void deveLancarExcecaoAoAtualizarDiariaInexistente() {
        Diaria diariaAtualizada = new Diaria(null, BigDecimal.valueOf(70.00), "DIARIA_ATUALIZADA", "Nova descrição", null);
        when(diariaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> diariaService.atualizarDiaria(99L, diariaAtualizada));
        verify(diariaRepository, never()).save(any(Diaria.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar atualizar diária com valor nulo")
    void deveLancarExcecaoAoAtualizarDiariaComValorNulo() {
        Diaria diariaAtualizada = new Diaria(null, null, "DIARIA_ATUALIZADA", "Nova descrição", null);
        when(diariaRepository.findById(1L)).thenReturn(Optional.of(diariaValida));

        assertThrows(DescricaoEmBrancoException.class, () -> diariaService.atualizarDiaria(1L, diariaAtualizada));
        verify(diariaRepository, never()).save(any(Diaria.class));
    }

    @Test
    @DisplayName("Deve deletar uma diária existente com sucesso")
    void deveDeletarDiariaExistente() {
        when(diariaRepository.findById(1L)).thenReturn(Optional.of(diariaValida));
        doNothing().when(diariaRepository).delete(diariaValida);

        assertDoesNotThrow(() -> diariaService.deletarDiaria(1L));

        verify(diariaRepository, times(1)).findById(1L);
        verify(diariaRepository, times(1)).delete(diariaValida);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar diária inexistente")
    void deveLancarExcecaoAoDeletarDiariaInexistente() {
        when(diariaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> diariaService.deletarDiaria(99L));
        verify(diariaRepository, never()).delete(any(Diaria.class));
    }

    @Test
    @DisplayName("Deve adicionar DiariaNoturna a uma Diaria existente sem DiariaNoturna")
    void deveAdicionarDiariaNoturnaAExistente() {
        Diaria diariaExistenteSemNoturna = new Diaria(1L, BigDecimal.valueOf(50.00), "DIARIA_COMUM", "Diária normal", null);
        DiariaNoturna novaDiariaNoturna = new DiariaNoturna(null, LocalTime.of(22, 0), LocalTime.of(6, 0), BigDecimal.valueOf(7.50), null);

        Diaria diariaAtualizada = new Diaria(null, BigDecimal.valueOf(50.00), "DIARIA_COMUM", "Diária normal", novaDiariaNoturna);

        when(diariaRepository.findById(1L)).thenReturn(Optional.of(diariaExistenteSemNoturna));
        when(diariaRepository.save(any(Diaria.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(diariaNoturnaRepository.save(any(DiariaNoturna.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Diaria resultado = diariaService.atualizarDiaria(1L, diariaAtualizada);

        assertNotNull(resultado.getDiariaNoturna());
        assertEquals(LocalTime.of(22, 0), resultado.getDiariaNoturna().getHoraInicio());
        assertEquals(LocalTime.of(6, 0), resultado.getDiariaNoturna().getHoraFim());
        assertEquals(BigDecimal.valueOf(7.50), resultado.getDiariaNoturna().getAdicionalNoturno());
        verify(diariaRepository, times(1)).save(diariaExistenteSemNoturna);
        verify(diariaNoturnaRepository, times(1)).save(novaDiariaNoturna);
    }


    @Test
    @DisplayName("Deve atualizar DiariaNoturna de uma Diaria existente")
    void deveAtualizarDiariaNoturnaDeExistente() {
        Diaria diariaExistenteComNoturna = new Diaria(1L, BigDecimal.valueOf(50.00), "DIARIA_COMUM", "Diária normal", diariaNoturnaValida);
        diariaNoturnaValida.setDiaria(diariaExistenteComNoturna);

        DiariaNoturna atualizacaoDiariaNoturna = new DiariaNoturna(null, LocalTime.of(23, 0), LocalTime.of(7, 0), BigDecimal.valueOf(10.00), null);
        Diaria diariaAtualizada = new Diaria(null, BigDecimal.valueOf(50.00), "DIARIA_COMUM", "Diária normal", atualizacaoDiariaNoturna);

        when(diariaRepository.findById(1L)).thenReturn(Optional.of(diariaExistenteComNoturna));
        when(diariaRepository.save(any(Diaria.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(diariaNoturnaRepository.save(any(DiariaNoturna.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Diaria resultado = diariaService.atualizarDiaria(1L, diariaAtualizada);

        assertNotNull(resultado.getDiariaNoturna());
        assertEquals(LocalTime.of(23, 0), resultado.getDiariaNoturna().getHoraInicio());
        assertEquals(LocalTime.of(7, 0), resultado.getDiariaNoturna().getHoraFim());
        assertEquals(BigDecimal.valueOf(10.00), resultado.getDiariaNoturna().getAdicionalNoturno());
        verify(diariaRepository, times(1)).save(diariaExistenteComNoturna);
        verify(diariaNoturnaRepository, times(1)).save(diariaNoturnaValida);
    }

    @Test
    @DisplayName("Deve remover DiariaNoturna de uma Diaria existente")
    void deveRemoverDiariaNoturnaDeExistente() {
        Diaria diariaExistenteComNoturna = new Diaria(1L, BigDecimal.valueOf(50.00), "DIARIA_COMUM", "Diária normal", diariaNoturnaValida);
        diariaNoturnaValida.setDiaria(diariaExistenteComNoturna);

        Diaria diariaAtualizada = new Diaria(null, BigDecimal.valueOf(50.00), "DIARIA_COMUM", "Diária normal", null);

        when(diariaRepository.findById(1L)).thenReturn(Optional.of(diariaExistenteComNoturna));
        when(diariaRepository.save(any(Diaria.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(diariaNoturnaRepository).delete(any(DiariaNoturna.class));

        Diaria resultado = diariaService.atualizarDiaria(1L, diariaAtualizada);

        assertNull(resultado.getDiariaNoturna());
        verify(diariaRepository, times(1)).save(diariaExistenteComNoturna);
        verify(diariaNoturnaRepository, times(1)).delete(diariaNoturnaValida);
    }
}