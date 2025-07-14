package com.estacionamento_tppe.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Mensalista;
import com.estacionamento.repository.MensalistaRepository;
import com.estacionamento.service.MensalistaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensalistaServiceTest {

    @Mock
    private MensalistaRepository mensalistaRepository;

    @InjectMocks
    private MensalistaService mensalistaService;

    private Mensalista mensalistaValido;

    @BeforeEach
    void setUp() {
        mensalistaValido = new Mensalista();
        mensalistaValido.setId(1L);
        mensalistaValido.setValor(new BigDecimal("250.00"));
        mensalistaValido.setPeriodoMeses(1);
        mensalistaValido.setDescricao("Mensalidade padrão");

        reset(mensalistaRepository);
    }

    @Test
    @DisplayName("Deve criar um mensalista válido com sucesso")
    void deveCriarMensalistaValido() {
        when(mensalistaRepository.save(any(Mensalista.class))).thenReturn(mensalistaValido);

        Mensalista salvo = mensalistaService.criarMensalista(mensalistaValido);

        assertNotNull(salvo);
        assertEquals(new BigDecimal("250.00"), salvo.getValor());
        verify(mensalistaRepository, times(1)).save(any(Mensalista.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar criar mensalista com valor nulo")
    void deveLancarExcecaoQuandoCriarMensalistaComValorNulo() {
        mensalistaValido.setValor(null);
        assertThrows(DescricaoEmBrancoException.class, () -> mensalistaService.criarMensalista(mensalistaValido));
        verify(mensalistaRepository, never()).save(any(Mensalista.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar criar mensalista com valor zero ou negativo")
    void deveLancarExcecaoQuandoCriarMensalistaComValorZeroOuNegativo() {
        mensalistaValido.setValor(BigDecimal.ZERO);
        assertThrows(DescricaoEmBrancoException.class, () -> mensalistaService.criarMensalista(mensalistaValido));
        mensalistaValido.setValor(new BigDecimal("-10.00"));
        assertThrows(DescricaoEmBrancoException.class, () -> mensalistaService.criarMensalista(mensalistaValido));
        verify(mensalistaRepository, never()).save(any(Mensalista.class));
    }

    @Test
    @DisplayName("Deve retornar mensalista por ID quando encontrado")
    void deveRetornarMensalistaPorIdQuandoEncontrado() {
        when(mensalistaRepository.findById(1L)).thenReturn(Optional.of(mensalistaValido));

        Mensalista encontrado = mensalistaService.buscarMensalistaPorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        verify(mensalistaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar mensalista por ID inexistente")
    void deveLancarExcecaoQuandoBuscarMensalistaPorIdInexistente() {
        when(mensalistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> 
        mensalistaService.buscarMensalistaPorId(99L));
        verify(mensalistaRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todos os mensalistas")
    void deveListarTodosMensalistas() {
        List<Mensalista> mensalistas = Arrays.asList(mensalistaValido, new Mensalista());
        when(mensalistaRepository.findAll()).thenReturn(mensalistas);

        List<Mensalista> lista = mensalistaService.listarTodosMensalistas();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(mensalistaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar um mensalista existente com sucesso")
    void deveAtualizarMensalistaExistente() {
        Mensalista atualizacaoPayload = new Mensalista();
        atualizacaoPayload.setValor(new BigDecimal("280.00"));
        atualizacaoPayload.setPeriodoMeses(3);
        atualizacaoPayload.setDescricao("Mensalidade trimestral");

        when(mensalistaRepository.findById(1L)).thenReturn(Optional.of(mensalistaValido));
        when(mensalistaRepository.save(any(Mensalista.class))).thenReturn(mensalistaValido);

        Mensalista atualizado = mensalistaService.atualizarMensalista(1L, atualizacaoPayload);

        assertNotNull(atualizado);
        assertEquals(new BigDecimal("280.00"), atualizado.getValor());
        assertEquals(3, atualizado.getPeriodoMeses());
        verify(mensalistaRepository, times(1)).findById(1L);
        verify(mensalistaRepository, times(1)).save(mensalistaValido);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar mensalista inexistente")
    void deveLancarExcecaoAoAtualizarMensalistaInexistente() {
        Mensalista atualizacaoPayload = new Mensalista();
        atualizacaoPayload.setValor(new BigDecimal("100.00"));

        when(mensalistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> 
        mensalistaService.atualizarMensalista(99L, atualizacaoPayload));
        verify(mensalistaRepository, never()).save(any(Mensalista.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException se valor for nulo na atualização")
    void deveLancarExcecaoSeValorNuloNaAtualizacao() {
        Mensalista atualizacaoPayload = new Mensalista();
        atualizacaoPayload.setValor(null);
        atualizacaoPayload.setPeriodoMeses(1);

        when(mensalistaRepository.findById(1L)).thenReturn(Optional.of(mensalistaValido));

        assertThrows(DescricaoEmBrancoException.class, () -> 
        mensalistaService.atualizarMensalista(1L, atualizacaoPayload));
        verify(mensalistaRepository, never()).save(any(Mensalista.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException se periodoMeses for zero ou negativo na atualização")
    void deveLancarExcecaoSePeriodoMesesInvalidoNaAtualizacao() {
        Mensalista atualizacaoPayload = new Mensalista();
        atualizacaoPayload.setValor(new BigDecimal("100.00"));
        atualizacaoPayload.setPeriodoMeses(0);

        when(mensalistaRepository.findById(1L)).thenReturn(Optional.of(mensalistaValido));

        assertThrows(DescricaoEmBrancoException.class, () -> 
        mensalistaService.atualizarMensalista(1L, atualizacaoPayload));
        verify(mensalistaRepository, never()).save(any(Mensalista.class));
    }

    @Test
    @DisplayName("Deve deletar um mensalista existente com sucesso")
    void deveDeletarMensalistaExistente() {
        when(mensalistaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(mensalistaRepository).deleteById(1L);

        assertDoesNotThrow(() -> mensalistaService.deletarMensalista(1L));

        verify(mensalistaRepository, times(1)).existsById(1L);
        verify(mensalistaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar mensalista inexistente")
    void deveLancarExcecaoAoDeletarMensalistaInexistente() {
        when(mensalistaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ObjetoNaoEncontradoException.class, () -> mensalistaService.deletarMensalista(99L));
        verify(mensalistaRepository, times(1)).existsById(99L);
        verify(mensalistaRepository, never()).deleteById(anyLong());
    }
}