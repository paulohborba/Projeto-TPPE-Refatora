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
import org.mockito.Mockito;

import java.time.LocalDateTime;
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
    private Contratante contratanteInexistente;

    @SuppressWarnings("unchecked")
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
        eventoValido.setDataInicio(LocalDateTime.of(2024, 7, 10, 20, 0));
        eventoValido.setDataFim(LocalDateTime.of(2024, 7, 10, 23, 0));
        eventoValido.setContratantes(new HashSet<>());

        contratanteInexistente = new Contratante();
        contratanteInexistente.setId(99L);
        contratanteInexistente.setNome("Contratante Inexistente");
        contratanteInexistente.setCpfCnpj("000.000.000-00");
        contratanteInexistente.setEmail("inexistente@email.com");
        contratanteInexistente.setEstacionamentos(new HashSet<>());
        contratanteInexistente.setEventos(new HashSet<>());

        reset(eventoRepository, contratanteRepository);
    }

    @Test
    @DisplayName("Deve criar um evento válido com sucesso")
    void deveCriarEventoValido() {
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoValido);

        Evento salvo = eventoService.criarEvento(eventoValido);

        assertNotNull(salvo);
        assertEquals("Show de Rock", salvo.getNomeEvento());
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
        eventoValido.setDataInicio(LocalDateTime.of(2024, 7, 10, 20, 0));
        eventoValido.setDataFim(LocalDateTime.of(2024, 7, 10, 19, 0));
        assertThrows(IllegalArgumentException.class, () -> eventoService.criarEvento(eventoValido));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve criar evento associando contratantes existentes")
    void deveCriarEventoComContratantes() {
        Evento eventoComContratante = new Evento();
        eventoComContratante.setNomeEvento("Festival");
        eventoComContratante.setDataInicio(LocalDateTime.of(2024, 8, 1, 10, 0));
        eventoComContratante.setDataFim(LocalDateTime.of(2024, 8, 3, 23, 0));
        eventoComContratante.setContratantes(new HashSet<>(Arrays.asList(contratantePadrao)));

        lenient().when(contratanteRepository.findById(contratantePadrao.getId())).thenReturn(Optional.of(contratantePadrao));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(invocation -> {
            Evento saved = invocation.getArgument(0);
            saved.setId(2L);
            saved.getContratantes().forEach(c -> c.getEventos().add(saved));
            return saved;
        });

        Evento salvo = eventoService.criarEvento(eventoComContratante);

        assertNotNull(salvo);
        assertFalse(salvo.getContratantes().isEmpty());
        assertEquals(1, salvo.getContratantes().size());
        assertEquals(contratantePadrao.getId(), salvo.getContratantes().iterator().next().getId());
        verify(contratanteRepository, times(1)).findById(contratantePadrao.getId());
        verify(eventoRepository, times(1)).save(any(Evento.class));
        assertTrue(contratantePadrao.getEventos().contains(salvo));
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao criar evento com Contratante inexistente")
    void deveLancarExcecaoAoCriarEventoComContratanteInexistente() {
        Evento eventoComContratanteInexistente = new Evento();
        eventoComContratanteInexistente.setNomeEvento("Evento X");
        eventoComContratanteInexistente.setDataInicio(LocalDateTime.now());
        eventoComContratanteInexistente.setDataFim(LocalDateTime.now().plusHours(2));
        eventoComContratanteInexistente.setContratantes(new HashSet<>(Arrays.asList(contratanteInexistente)));

        lenient().when(contratanteRepository.findById(contratanteInexistente.getId())).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> eventoService.criarEvento(eventoComContratanteInexistente));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve retornar evento por ID quando encontrado")
    void deveRetornarEventoPorIdQuandoEncontrado() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoValido));

        Evento encontrado = eventoService.buscarEventoPorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
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
        List<Evento> eventos = Arrays.asList(eventoValido, new Evento());
        when(eventoRepository.findAll()).thenReturn(eventos);

        List<Evento> lista = eventoService.listarTodosEventos();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(eventoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar um evento existente com sucesso")
    void deveAtualizarEventoExistente() {
        Contratante contratanteReal = new Contratante(10L, "Produtora de Eventos Ltda.", "11.222.333/0001-44", "contato@produtora.com", "tel", new HashSet<>(), new HashSet<>());
        Contratante contratanteSpy = Mockito.spy(contratanteReal);

        Evento eventoExistente = new Evento(1L, "Show Antigo", "Desc Antiga", LocalDateTime.now(), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventoExistente.addContratante(contratanteSpy); // Associa o spy ao evento existente

        Evento eventoAtualizadoPayload = new Evento();
        eventoAtualizadoPayload.setNomeEvento("Show Atualizado");
        eventoAtualizadoPayload.setDescricao("Descrição nova");
        eventoAtualizadoPayload.setDataInicio(LocalDateTime.of(2024, 7, 10, 20, 0));
        eventoAtualizadoPayload.setDataFim(LocalDateTime.of(2024, 7, 11, 0, 0));
        eventoAtualizadoPayload.setContratantes(new HashSet<>(Arrays.asList(contratanteSpy))); // Mantém o mesmo contratante

        lenient().when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoExistente));
        lenient().when(contratanteRepository.findById(contratanteSpy.getId())).thenReturn(Optional.of(contratanteSpy));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Evento atualizado = eventoService.atualizarEvento(1L, eventoAtualizadoPayload);

        assertNotNull(atualizado);
        assertEquals("Show Atualizado", atualizado.getNomeEvento());
        assertEquals("Descrição nova", atualizado.getDescricao());
        assertEquals(LocalDateTime.of(2024, 7, 11, 0, 0), atualizado.getDataFim());
        assertTrue(atualizado.getContratantes().contains(contratanteSpy));

        // Nenhuma chamada a removeEvento ou addEvento no spy do contratante para este cenário (manter associação)
        verify(contratanteSpy, never()).removeEvento(any(Evento.class));
        verify(contratanteSpy, never()).addEvento(any(Evento.class));

        verify(eventoRepository, times(1)).findById(1L);
        verify(contratanteRepository, times(1)).findById(contratanteSpy.getId());
        verify(eventoRepository, times(1)).save(eventoExistente);
    }


    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar evento inexistente")
    void deveLancarExcecaoAoAtualizarEventoInexistente() {
        Evento eventoAtualizado = new Evento();
        eventoAtualizado.setNomeEvento("Nome");
        eventoAtualizado.setDataInicio(LocalDateTime.now());
        eventoAtualizado.setDataFim(LocalDateTime.now().plusHours(1));
        eventoAtualizado.setContratantes(new HashSet<>());

        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> eventoService.atualizarEvento(99L, eventoAtualizado));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar atualizar evento com nome em branco")
    void deveLancarExcecaoAoAtualizarEventoComNomeEmBranco() {
        Evento eventoAtualizado = new Evento();
        eventoAtualizado.setNomeEvento("");
        eventoAtualizado.setDataInicio(LocalDateTime.now());
        eventoAtualizado.setDataFim(LocalDateTime.now().plusHours(1));
        eventoAtualizado.setContratantes(new HashSet<>());

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoValido));

        assertThrows(DescricaoEmBrancoException.class, () -> eventoService.atualizarEvento(1L, eventoAtualizado));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se dataFim for anterior a dataInicio na atualização")
    void deveLancarExcecaoSeDataFimAnteriorAInicioNaAtualizacao() {
        Evento eventoAtualizado = new Evento();
        eventoAtualizado.setNomeEvento("Nome");
        eventoAtualizado.setDataInicio(LocalDateTime.of(2024, 7, 10, 20, 0));
        eventoAtualizado.setDataFim(LocalDateTime.of(2024, 7, 10, 19, 0));
        eventoAtualizado.setContratantes(new HashSet<>());

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoValido));

        assertThrows(IllegalArgumentException.class, () -> eventoService.atualizarEvento(1L, eventoAtualizado));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve deletar um evento existente com sucesso e desassociar de contratantes")
    void deveDeletarEventoExistente() {
        // Crie uma instância REAL de Contratante e um SPY dela
        Contratante contratanteReal = new Contratante();
        contratanteReal.setId(10L);
        contratanteReal.setNome("Contratante Real");
        contratanteReal.setCpfCnpj("111.111.111-11");
        contratanteReal.setEmail("real@email.com");
        contratanteReal.setEstacionamentos(new HashSet<>());
        contratanteReal.setEventos(new HashSet<>());
        Contratante contratanteSpy = Mockito.spy(contratanteReal);


        Evento eventoParaDeletar = new Evento();
        eventoParaDeletar.setId(1L);
        eventoParaDeletar.setNomeEvento("Evento para Deletar");
        eventoParaDeletar.setDataInicio(LocalDateTime.now());
        eventoParaDeletar.setDataFim(LocalDateTime.now().plusHours(1));
        eventoParaDeletar.setContratantes(new HashSet<>());

        // Associa o contratanteSpy ao evento e vice-versa para simular o estado inicial
        eventoParaDeletar.addContratante(contratanteSpy);


        lenient().when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoParaDeletar));
        doNothing().when(eventoRepository).delete(eventoParaDeletar);

        assertDoesNotThrow(() -> eventoService.deletarEvento(1L));

        verify(eventoRepository, times(1)).findById(1L);
        verify(eventoRepository, times(1)).delete(eventoParaDeletar);
        // Verifica se o método removeEvento foi chamado no SPY do contratante
        verify(contratanteSpy, times(1)).removeEvento(eventoParaDeletar);

        // Verifica o estado da coleção no SPY após a execução do serviço
        assertFalse(contratanteSpy.getEventos().contains(eventoParaDeletar));
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar evento inexistente")
    void deveLancarExcecaoAoDeletarEventoInexistente() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> eventoService.deletarEvento(99L));
        verify(eventoRepository, never()).delete(any(Evento.class));
    }
}