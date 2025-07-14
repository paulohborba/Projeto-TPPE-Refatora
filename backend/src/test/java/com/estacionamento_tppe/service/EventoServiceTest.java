package com.estacionamento_tppe.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Contratante;
import com.estacionamento.model.Evento;
import com.estacionamento.repository.ContratanteRepository;
import com.estacionamento.repository.EventoRepository;
import com.estacionamento.service.EventoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private ContratanteRepository contratanteRepository;

    @InjectMocks
    private EventoService eventoService;

    private Evento eventoValido;
    private Contratante contratantePadrao;

    @BeforeEach
    void setUp() {
        contratantePadrao = new Contratante();
        contratantePadrao.setId(10L);
        contratantePadrao.setNome("Produtora de Eventos Ltda.");
        contratantePadrao.setCpfCnpj("11.222.333/0001-44");
        contratantePadrao.setEmail("contato@produtora.com");
        contratantePadrao.setEstacionamentos(new HashSet<>());
        contratantePadrao.setEventos(new HashSet<>());

        eventoValido = new Evento();
        eventoValido.setId(1L);
        eventoValido.setNomeEvento("Show de Rock");
        eventoValido.setDescricao("Show imperdível");
        eventoValido.setDataInicio(LocalDate.of(2024, 7, 10));
        eventoValido.setHoraInicio(LocalTime.of(10, 0, 0));
        eventoValido.setDataFim(LocalDate.of(2024, 7, 10));
        eventoValido.setHoraFim(LocalTime.of(23, 0, 0));
        eventoValido.setContratantes(new HashSet<>());
    }

    @Test
    @DisplayName("Deve criar um evento válido com sucesso")
    void deveCriarEventoValido() {
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoValido);

        Evento resultado = eventoService.criarEvento(eventoValido);

        assertNotNull(resultado);
        assertEquals("Show de Rock", resultado.getNomeEvento());
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao criar evento com nome em branco")
    void deveLancarExcecaoQuandoCriarEventoComNomeEmBranco() {
        eventoValido.setNomeEvento("");
        
        assertThrows(DescricaoEmBrancoException.class, () -> eventoService.criarEvento(eventoValido));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao criar evento com dataInicio nula")
    void deveLancarExcecaoQuandoCriarEventoComDataInicioNula() {
        eventoValido.setDataInicio(null);
        
        assertThrows(DescricaoEmBrancoException.class, () -> eventoService.criarEvento(eventoValido));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao criar evento com dataFim anterior a dataInicio")
    void deveLancarExcecaoQuandoCriarEventoComDataFimAnteriorADataInicio() {
        eventoValido.setDataInicio(LocalDate.of(2024, 7, 10));
        eventoValido.setHoraInicio(LocalTime.of(20, 0, 0));
        eventoValido.setDataFim(LocalDate.of(2024, 7, 10));
        eventoValido.setHoraFim(LocalTime.of(19, 0, 0));

        assertThrows(IllegalArgumentException.class, () -> eventoService.criarEvento(eventoValido));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve criar evento associando contratantes existentes")
    void deveCriarEventoComContratantes() {
        Evento evento = new Evento();
        evento.setNomeEvento("Festival");
        evento.setDataInicio(LocalDate.of(2024, 8, 1));
        evento.setHoraInicio(LocalTime.of(10, 0, 0));
        evento.setDataFim(LocalDate.of(2024, 8, 1));
        evento.setHoraFim(LocalTime.of(23, 0, 0));
        evento.setContratantes(new HashSet<>(Arrays.asList(contratantePadrao)));

        when(contratanteRepository.findById(10L)).thenReturn(Optional.of(contratantePadrao));
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        Evento resultado = eventoService.criarEvento(evento);

        assertNotNull(resultado);
        assertEquals("Festival", resultado.getNomeEvento());
        verify(contratanteRepository, times(1)).findById(10L);
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao criar evento com Contratante inexistente")
    void deveLancarExcecaoAoCriarEventoComContratanteInexistente() {
        Contratante contratanteInexistente = new Contratante();
        contratanteInexistente.setId(99L);
        
        Evento evento = new Evento();
        evento.setNomeEvento("Evento X");
        evento.setDataInicio(LocalDate.of(2024, 8, 1));
        evento.setHoraInicio(LocalTime.of(10, 0, 0));
        evento.setDataFim(LocalDate.of(2024, 8, 1));
        evento.setHoraFim(LocalTime.of(23, 0, 0));
        evento.setContratantes(new HashSet<>(Arrays.asList(contratanteInexistente)));

        when(contratanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> eventoService.criarEvento(evento));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve retornar evento por ID quando encontrado")
    void deveRetornarEventoPorIdQuandoEncontrado() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoValido));

        Evento resultado = eventoService.buscarEventoPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(eventoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar evento por ID inexistente")
    void deveLancarExcecaoQuandoBuscarEventoPorIdInexistente() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> eventoService.buscarEventoPorId(99L));
        verify(eventoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todos os eventos")
    void deveListarTodosEventos() {
        List<Evento> eventos = Arrays.asList(eventoValido);
        when(eventoRepository.findAll()).thenReturn(eventos);

        List<Evento> resultado = eventoService.listarTodosEventos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(eventoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar um evento existente com sucesso")
    void deveAtualizarEventoExistente() {
        Evento eventoExistente = new Evento();
        eventoExistente.setId(1L);
        eventoExistente.setNomeEvento("Show Antigo");
        eventoExistente.setDescricao("Desc Antiga");
        eventoExistente.setDataInicio(LocalDate.of(2024, 7, 10));
        eventoExistente.setHoraInicio(LocalTime.of(10, 0, 0));
        eventoExistente.setDataFim(LocalDate.of(2024, 7, 10));
        eventoExistente.setHoraFim(LocalTime.of(23, 0, 0));
        eventoExistente.setContratantes(new HashSet<>());

        Evento eventoAtualizado = new Evento();
        eventoAtualizado.setNomeEvento("Show Atualizado");
        eventoAtualizado.setDescricao("Descrição nova");
        eventoAtualizado.setDataInicio(LocalDate.of(2024, 10, 1));
        eventoAtualizado.setHoraInicio(LocalTime.of(20, 0, 0));
        eventoAtualizado.setDataFim(LocalDate.of(2024, 10, 1));
        eventoAtualizado.setHoraFim(LocalTime.of(23, 0, 0));
        eventoAtualizado.setContratantes(new HashSet<>());

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoExistente));
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoExistente);

        Evento resultado = eventoService.atualizarEvento(1L, eventoAtualizado);

        assertNotNull(resultado);
        verify(eventoRepository, times(1)).findById(1L);
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar evento inexistente")
    void deveLancarExcecaoAoAtualizarEventoInexistente() {
        Evento eventoAtualizado = new Evento();
        eventoAtualizado.setNomeEvento("Nome");
        eventoAtualizado.setDataInicio(LocalDate.of(2024, 8, 1));
        eventoAtualizado.setHoraInicio(LocalTime.of(10, 0, 0));
        eventoAtualizado.setDataFim(LocalDate.of(2024, 8, 1));
        eventoAtualizado.setHoraFim(LocalTime.of(23, 0, 0));
        eventoAtualizado.setContratantes(new HashSet<>());

        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () ->
            eventoService.atualizarEvento(99L, eventoAtualizado));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar atualizar evento com nome em branco")
    void deveLancarExcecaoAoAtualizarEventoComNomeEmBranco() {
        Evento eventoAtualizado = new Evento();
        eventoAtualizado.setNomeEvento("");
        eventoAtualizado.setDataInicio(LocalDate.of(2024, 8, 1));
        eventoAtualizado.setHoraInicio(LocalTime.of(10, 0, 0));
        eventoAtualizado.setDataFim(LocalDate.of(2024, 8, 1));
        eventoAtualizado.setHoraFim(LocalTime.of(23, 0, 0));
        eventoAtualizado.setContratantes(new HashSet<>());

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoValido));

        assertThrows(DescricaoEmBrancoException.class, () ->
            eventoService.atualizarEvento(1L, eventoAtualizado));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se dataFim for anterior a dataInicio na atualização")
    void deveLancarExcecaoSeDataFimAnteriorAInicioNaAtualizacao() {
        Evento eventoAtualizado = new Evento();
        eventoAtualizado.setNomeEvento("Nome");
        eventoAtualizado.setDataInicio(LocalDate.of(2024, 7, 12));
        eventoAtualizado.setHoraInicio(LocalTime.of(20, 0, 0));
        eventoAtualizado.setDataFim(LocalDate.of(2024, 7, 10));
        eventoAtualizado.setHoraFim(LocalTime.of(19, 0, 0));
        eventoAtualizado.setContratantes(new HashSet<>());

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoValido));

        assertThrows(IllegalArgumentException.class, () ->
            eventoService.atualizarEvento(1L, eventoAtualizado));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve deletar um evento existente com sucesso")
    void deveDeletarEventoExistente() {
        Evento eventoParaDeletar = new Evento();
        eventoParaDeletar.setId(1L);
        eventoParaDeletar.setNomeEvento("Evento para Deletar");
        eventoParaDeletar.setDataInicio(LocalDate.of(2024, 8, 1));
        eventoParaDeletar.setHoraInicio(LocalTime.of(10, 0, 0));
        eventoParaDeletar.setDataFim(LocalDate.of(2024, 8, 1));
        eventoParaDeletar.setHoraFim(LocalTime.of(23, 0, 0));
        eventoParaDeletar.setContratantes(new HashSet<>());

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoParaDeletar));
        doNothing().when(eventoRepository).delete(eventoParaDeletar);

        assertDoesNotThrow(() -> eventoService.deletarEvento(1L));

        verify(eventoRepository, times(1)).findById(1L);
        verify(eventoRepository, times(1)).delete(eventoParaDeletar);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar evento inexistente")
    void deveLancarExcecaoAoDeletarEventoInexistente() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> eventoService.deletarEvento(99L));
        verify(eventoRepository, never()).delete(any(Evento.class));
    }
}