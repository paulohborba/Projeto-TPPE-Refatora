package com.estacionamento_tppe.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Contratante;
import com.estacionamento.model.Estacionamento;
import com.estacionamento.model.Evento;
import com.estacionamento.repository.ContratanteRepository;
import com.estacionamento.repository.EstacionamentoRepository;
import com.estacionamento.repository.EventoRepository;
import com.estacionamento.service.ContratanteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratanteServiceTest {

    @Mock
    private ContratanteRepository contratanteRepository;
    @Mock
    private EstacionamentoRepository estacionamentoRepository;
    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private ContratanteService contratanteService;

    private Contratante contratanteValido;
    private Estacionamento estacionamentoPadrao;
    private Evento eventoPadrao;
    private Contratante contratanteComAssociacoes;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        estacionamentoPadrao = new Estacionamento(1L, "Estacionamento Teste", "Rua Teste, 123", 50,
                LocalTime.of(8, 0), LocalTime.of(18, 0), new HashSet<>());
        eventoPadrao = new Evento(1L, "Evento Teste", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                "Descrição do evento teste", new HashSet<>());

        contratanteValido = new Contratante();
        contratanteValido.setId(1L);
        contratanteValido.setNome("Empresa Teste");
        contratanteValido.setCpfCnpj("12.345.678/0001-90");
contratanteValido.setEmail("teste@empresa.com");
        contratanteValido.setTelefone("11987654321");
        contratanteValido.setEstacionamentos(new HashSet<>());
        contratanteValido.setEventos(new HashSet<>());

        contratanteComAssociacoes = new Contratante();
        contratanteComAssociacoes.setId(2L);
        contratanteComAssociacoes.setNome("Contratante com Associações");
        contratanteComAssociacoes.setCpfCnpj("98.765.432/0001-21");
        contratanteComAssociacoes.setEmail("associado@empresa.com");
        contratanteComAssociacoes.setTelefone("11912345678");
        contratanteComAssociacoes.setEstacionamentos(new HashSet<>(Arrays.asList(estacionamentoPadrao)));
        contratanteComAssociacoes.setEventos(new HashSet<>(Arrays.asList(eventoPadrao)));

        estacionamentoPadrao.getContratantes().add(contratanteComAssociacoes);
        eventoPadrao.getContratantes().add(contratanteComAssociacoes);

        reset(contratanteRepository, estacionamentoRepository, eventoRepository);
    }

    @Test
    @DisplayName("Deve criar um contratante válido com sucesso")
    void deveCriarContratanteValido() {
        when(contratanteRepository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
        when(contratanteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(contratanteRepository.save(any(Contratante.class))).thenReturn(contratanteValido);

        Contratante salvo = contratanteService.criarContratante(contratanteValido);

        assertNotNull(salvo);
        assertEquals("Empresa Teste", salvo.getNome());
        verify(contratanteRepository, times(1)).save(any(Contratante.class));
    }

    @Test
    @DisplayName("Deve criar contratante associando estacionamentos e eventos existentes")
    void deveCriarContratanteAssociandoEstacionamentosEEventosExistentes() {
        Contratante novoContratante = new Contratante();
        novoContratante.setNome("Novo Contratante");
        novoContratante.setCpfCnpj("11.222.333/0001-44");
        novoContratante.setEmail("novo@email.com");
        novoContratante.setEstacionamentos(new HashSet<>(Arrays.asList(estacionamentoPadrao)));
        novoContratante.setEventos(new HashSet<>(Arrays.asList(eventoPadrao)));

        lenient().when(contratanteRepository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
        lenient().when(contratanteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        lenient().when(estacionamentoRepository.findById(estacionamentoPadrao.getId())).thenReturn(Optional.of(estacionamentoPadrao));
        lenient().when(eventoRepository.findById(eventoPadrao.getId())).thenReturn(Optional.of(eventoPadrao));
        when(contratanteRepository.save(any(Contratante.class))).thenAnswer(invocation -> {
            Contratante saved = invocation.getArgument(0);
            saved.setId(3L);
            saved.getEstacionamentos().forEach(e -> e.getContratantes().add(saved));
            saved.getEventos().forEach(e -> e.getContratantes().add(saved));
            return saved;
        });

        Contratante salvo = contratanteService.criarContratante(novoContratante);

        assertNotNull(salvo);
        assertFalse(salvo.getEstacionamentos().isEmpty());
        assertFalse(salvo.getEventos().isEmpty());
        assertEquals(1, salvo.getEstacionamentos().size());
        assertEquals(1, salvo.getEventos().size());
        assertTrue(salvo.getEstacionamentos().contains(estacionamentoPadrao));
        assertTrue(salvo.getEventos().contains(eventoPadrao));
        assertTrue(estacionamentoPadrao.getContratantes().contains(salvo));
        assertTrue(eventoPadrao.getContratantes().contains(salvo));
        verify(estacionamentoRepository, times(1)).findById(estacionamentoPadrao.getId());
        verify(eventoRepository, times(1)).findById(eventoPadrao.getId());
        verify(contratanteRepository, times(1)).save(any(Contratante.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao criar contratante com nome em branco")
    void deveLancarExcecaoQuandoCriarContratanteComNomeEmBranco() {
        contratanteValido.setNome("");
        assertThrows(DescricaoEmBrancoException.class, () -> contratanteService.criarContratante(contratanteValido));
        verify(contratanteRepository, never()).save(any(Contratante.class));
    }

    @Test
    @DisplayName("Deve retornar contratante por ID quando encontrado")
    void deveRetornarContratantePorIdQuandoEncontrado() {
        when(contratanteRepository.findById(1L)).thenReturn(Optional.of(contratanteValido));

        Contratante encontrado = contratanteService.buscarContratantePorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        verify(contratanteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar contratante por ID inexistente")
    void deveLancarExcecaoAoBuscarContratantePorIdInexistente() {
        when(contratanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> contratanteService.buscarContratantePorId(99L));
        verify(contratanteRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todos os contratantes")
    void deveListarTodosContratantes() {
        List<Contratante> contratantes = Arrays.asList(contratanteValido, new Contratante());
        when(contratanteRepository.findAll()).thenReturn(contratantes);

        List<Contratante> lista = contratanteService.listarTodosContratantes();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(contratanteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar contratante e gerenciar associações com estacionamentos e eventos")
    void deveAtualizarContratanteEGerenciarAssociacoesComEstacionamentosEEventos() {
        Estacionamento estExistente1 = Mockito.spy(new Estacionamento(10L, "E1", "End1", 10, LocalTime.now(), LocalTime.now(), new HashSet<>()));
        Estacionamento estExistente2 = Mockito.spy(new Estacionamento(11L, "E2", "End2", 20, LocalTime.now(), LocalTime.now(), new HashSet<>()));
        Evento evtExistente1 = Mockito.spy(new Evento(20L, "Ev1", LocalDateTime.now(), LocalDateTime.now(), "Desc1", new HashSet<>()));
        Evento evtExistente2 = Mockito.spy(new Evento(21L, "Ev2", LocalDateTime.now(), LocalDateTime.now(), "Desc2", new HashSet<>()));

        // Contratante existente como um SPY
        Contratante contratanteExistenteSpy = Mockito.spy(new Contratante(1L, "Original", "123", "orig@mail.com", "tel",
                new HashSet<>(), new HashSet<>())); // Coleções vazias aqui, vamos adicionar via addEstacionamento/addEvento

        contratanteExistenteSpy.addEstacionamento(estExistente1);
        contratanteExistenteSpy.addEvento(evtExistente1);

        // Payload de atualização: adiciona estExistente2, remove estExistente1, mantém evtExistente1, adiciona evtExistente2
        Contratante contratanteAtualizadoPayload = new Contratante();
        contratanteAtualizadoPayload.setNome("Atualizado");
        contratanteAtualizadoPayload.setCpfCnpj("987");
        contratanteAtualizadoPayload.setEmail("upd@mail.com");
        contratanteAtualizadoPayload.setEstacionamentos(new HashSet<>(Arrays.asList(estExistente2)));
        contratanteAtualizadoPayload.setEventos(new HashSet<>(Arrays.asList(evtExistente1, evtExistente2)));


        lenient().when(contratanteRepository.findById(1L)).thenReturn(Optional.of(contratanteExistenteSpy));
        lenient().when(contratanteRepository.findByCpfCnpj(anyString())).thenReturn(Optional.of(contratanteExistenteSpy));
        lenient().when(contratanteRepository.findByEmail(anyString())).thenReturn(Optional.of(contratanteExistenteSpy));

        lenient().when(estacionamentoRepository.findById(estExistente1.getId())).thenReturn(Optional.of(estExistente1));
        lenient().when(estacionamentoRepository.findById(estExistente2.getId())).thenReturn(Optional.of(estExistente2));
        lenient().when(eventoRepository.findById(evtExistente1.getId())).thenReturn(Optional.of(evtExistente1));
        lenient().when(eventoRepository.findById(evtExistente2.getId())).thenReturn(Optional.of(evtExistente2));

        when(contratanteRepository.save(any(Contratante.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Contratante result = contratanteService.atualizarContratante(1L, contratanteAtualizadoPayload);

        assertNotNull(result);
        assertEquals("Atualizado", result.getNome());
        assertEquals("987", result.getCpfCnpj());
        assertEquals("upd@mail.com", result.getEmail());

        // Verifica estacionamentos (Contratante)
        assertEquals(1, result.getEstacionamentos().size());
        assertTrue(result.getEstacionamentos().contains(estExistente2));
        assertFalse(result.getEstacionamentos().contains(estExistente1));

        // Verifica eventos (Contratante)
        assertEquals(2, result.getEventos().size());
        assertTrue(result.getEventos().contains(evtExistente1));
        assertTrue(result.getEventos().contains(evtExistente2));

        // Verifica a bidirecionalidade nos SPYs de Estacionamento/Evento
        // estExistente1 foi removido, então não deve mais conter o contratante
        verify(estExistente1, times(1)).removeContratante(contratanteExistenteSpy);
        assertFalse(estExistente1.getContratantes().contains(contratanteExistenteSpy));

        // estExistente2 foi adicionado
        verify(estExistente2, times(1)).addContratante(contratanteExistenteSpy);
        assertTrue(estExistente2.getContratantes().contains(contratanteExistenteSpy));

        // evtExistente1 foi mantido (não deve ter sido chamado remove ou add)
        verify(evtExistente1, never()).removeContratante(contratanteExistenteSpy);
        verify(evtExistente1, never()).addContratante(contratanteExistenteSpy);
        assertTrue(evtExistente1.getContratantes().contains(contratanteExistenteSpy));

        // evtExistente2 foi adicionado
        verify(evtExistente2, times(1)).addContratante(contratanteExistenteSpy);
        assertTrue(evtExistente2.getContratantes().contains(contratanteExistenteSpy));


        verify(contratanteRepository, times(1)).findById(1L);
        verify(contratanteRepository, times(1)).save(contratanteExistenteSpy);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar contratante inexistente")
    void deveLancarExcecaoAoAtualizarContratanteInexistente() {
        Contratante contratanteAtualizado = new Contratante();
        contratanteAtualizado.setNome("Nome");
        contratanteAtualizado.setCpfCnpj("CPF");
        contratanteAtualizado.setEmail("email");
        contratanteAtualizado.setEstacionamentos(new HashSet<>());
        contratanteAtualizado.setEventos(new HashSet<>());

        when(contratanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> contratanteService.atualizarContratante(99L, contratanteAtualizado));
        verify(contratanteRepository, never()).save(any(Contratante.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar atualizar contratante com email em branco")
    void deveLancarExcecaoAoAtualizarContratanteComEmailEmBranco() {
        Contratante contratanteAtualizado = new Contratante();
        contratanteAtualizado.setNome("Nome");
        contratanteAtualizado.setCpfCnpj("CPF");
        contratanteAtualizado.setEmail("");

        when(contratanteRepository.findById(1L)).thenReturn(Optional.of(contratanteValido));

        assertThrows(DescricaoEmBrancoException.class, () -> contratanteService.atualizarContratante(1L, contratanteAtualizado));
        verify(contratanteRepository, never()).save(any(Contratante.class));
    }

    @Test
    @DisplayName("Deve deletar um contratante existente com sucesso e desassociar de estacionamentos/eventos")
    void deveDeletarContratanteExistente() {
        // Crie instâncias REAIS de Estacionamento e Evento
        Estacionamento estacionamentoReal = new Estacionamento(100L, "Estacionamento Real", "Rua Real", 10, LocalTime.now(), LocalTime.now(), new HashSet<>());
        Evento eventoReal = new Evento(200L, "Evento Real", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Desc", new HashSet<>());

        // Crie o contratante a ser deletado como um SPY de uma instância REAL
        Contratante contratanteParaDeletarSpy = Mockito.spy(new Contratante(3L, "Para Deletar", "00.000.000/0000-00", "del@email.com", "tel", new HashSet<>(), new HashSet<>()));

        // Associa as entidades reais ao SPY do contratante usando os métodos add
        contratanteParaDeletarSpy.addEstacionamento(estacionamentoReal);
        contratanteParaDeletarSpy.addEvento(eventoReal);

        // Crie SPYs para as entidades associadas que serão removidas
        Estacionamento estacionamentoSpy = Mockito.spy(estacionamentoReal);
        Evento eventoSpy = Mockito.spy(eventoReal);

        // Substitua os objetos reais no Set do contratanteParaDeletarSpy pelos SPYs
        contratanteParaDeletarSpy.setEstacionamentos(new HashSet<>(Arrays.asList(estacionamentoSpy)));
        contratanteParaDeletarSpy.setEventos(new HashSet<>(Arrays.asList(eventoSpy)));

        // Mocks para os repositórios (lenient para evitar UnnecessaryStubbingException)
        lenient().when(contratanteRepository.findById(3L)).thenReturn(Optional.of(contratanteParaDeletarSpy));
        doNothing().when(contratanteRepository).delete(contratanteParaDeletarSpy);

        // Ação: deletar contratante
        assertDoesNotThrow(() -> contratanteService.deletarContratante(3L));

        // Verificações
        verify(contratanteRepository, times(1)).findById(3L);
        verify(contratanteRepository, times(1)).delete(contratanteParaDeletarSpy);

        // Verifica se os métodos removeContratante foram chamados nos SPYs de Estacionamento e Evento
        verify(estacionamentoSpy, times(1)).removeContratante(contratanteParaDeletarSpy);
        verify(eventoSpy, times(1)).removeContratante(contratanteParaDeletarSpy);

        // Verifica o estado das coleções bidirecionais
        assertFalse(estacionamentoSpy.getContratantes().contains(contratanteParaDeletarSpy));
        assertFalse(eventoSpy.getContratantes().contains(contratanteParaDeletarSpy));
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar contratante inexistente")
    void deveLancarExcecaoAoDeletarContratanteInexistente() {
        when(contratanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> contratanteService.deletarContratante(99L));
        verify(contratanteRepository, never()).delete(any(Contratante.class));
    }
}