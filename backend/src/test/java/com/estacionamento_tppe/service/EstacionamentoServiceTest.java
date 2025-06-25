package com.estacionamento_tppe.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Contratante;
import com.estacionamento.model.Estacionamento;
import com.estacionamento.repository.ContratanteRepository;
import com.estacionamento.repository.EstacionamentoRepository;
import com.estacionamento.service.EstacionamentoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
class EstacionamentoServiceTest {

    @Mock
    private EstacionamentoRepository estacionamentoRepository;
    @Mock
    private ContratanteRepository contratanteRepository;

    @InjectMocks
    private EstacionamentoService estacionamentoService;

    private Estacionamento estacionamentoValido;
    private Contratante contratantePadrao;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        contratantePadrao = new Contratante();
        contratantePadrao.setId(10L);
        contratantePadrao.setNome("Contratante Teste");
        contratantePadrao.setCpfCnpj("11.222.333/0001-44");
        contratantePadrao.setEmail("contrato@teste.com");
        contratantePadrao.setEstacionamentos(new HashSet<>());
        contratantePadrao.setEventos(new HashSet<>());

        estacionamentoValido = new Estacionamento();
        estacionamentoValido.setId(1L);
        estacionamentoValido.setNome("Estacionamento Teste");
        estacionamentoValido.setEndereco("Rua dos Testes, 123");
        estacionamentoValido.setCapacidade(50);
        estacionamentoValido.setHoraAbertura(LocalTime.of(8, 0));
        estacionamentoValido.setHoraFechamento(LocalTime.of(18, 0));
        estacionamentoValido.setContratantes(new HashSet<>());

        reset(estacionamentoRepository, contratanteRepository);
    }

    @Test
    @DisplayName("Deve criar um estacionamento válido com sucesso")
    void deveCriarEstacionamentoValido() {
        lenient().when(estacionamentoRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenReturn(estacionamentoValido);

        Estacionamento salvo = estacionamentoService.criarEstacionamento(estacionamentoValido);

        assertNotNull(salvo);
        assertEquals("Estacionamento Teste", salvo.getNome());
        verify(estacionamentoRepository, times(1)).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve criar estacionamento associando contratantes existentes")
    void deveCriarEstacionamentoAssociandoContratantes() {
        Estacionamento estComContratante = new Estacionamento();
        estComContratante.setNome("Estacionamento com Contratante");
        estComContratante.setEndereco("Endereço com Contratante");
        estComContratante.setCapacidade(30);
        estComContratante.setHoraAbertura(LocalTime.of(7, 0));
    	estComContratante.setHoraFechamento(LocalTime.of(19, 0));
        estComContratante.setContratantes(new HashSet<>(Arrays.asList(contratantePadrao)));

        lenient().when(contratanteRepository.findById(contratantePadrao.getId())).thenReturn(Optional.of(contratantePadrao));
        lenient().when(estacionamentoRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenAnswer(invocation -> {
            Estacionamento saved = invocation.getArgument(0);
            saved.setId(2L);
            saved.getContratantes().forEach(c -> c.getEstacionamentos().add(saved));
            return saved;
        });

        Estacionamento salvo = estacionamentoService.criarEstacionamento(estComContratante);

        assertNotNull(salvo);
        assertFalse(salvo.getContratantes().isEmpty());
        assertEquals(1, salvo.getContratantes().size());
        assertTrue(salvo.getContratantes().contains(contratantePadrao));
        assertTrue(contratantePadrao.getEstacionamentos().contains(salvo));
        verify(contratanteRepository, times(1)).findById(contratantePadrao.getId());
        verify(estacionamentoRepository, times(1)).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao criar estacionamento com Contratante inexistente")
    void deveLancarExcecaoAoCriarEstacionamentoComContratanteInexistente() {
        Contratante contratanteInexistente = new Contratante();
        contratanteInexistente.setId(99L);

        Estacionamento estComContratanteInexistente = new Estacionamento();
        estComContratanteInexistente.setNome("Estacionamento Invalido");
        estComContratanteInexistente.setEndereco("Endereço Invalido");
        estComContratanteInexistente.setCapacidade(10);
        estComContratanteInexistente.setHoraAbertura(LocalTime.of(8, 0));
        estComContratanteInexistente.setHoraFechamento(LocalTime.of(18, 0));
        estComContratanteInexistente.setContratantes(new HashSet<>(Arrays.asList(contratanteInexistente)));

        lenient().when(estacionamentoRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(contratanteRepository.findById(contratanteInexistente.getId())).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> estacionamentoService.criarEstacionamento(estComContratanteInexistente));
        verify(estacionamentoRepository, never()).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao criar estacionamento com nome em branco")
    void deveLancarExcecaoQuandoCriarEstacionamentoComNomeEmBranco() {
        estacionamentoValido.setNome("");
        assertThrows(DescricaoEmBrancoException.class, () -> estacionamentoService.criarEstacionamento(estacionamentoValido));
        verify(estacionamentoRepository, never()).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando capacidade for zero ou negativa")
    void deveLancarExcecaoQuandoCapacidadeInvalida() {
        estacionamentoValido.setEndereco("Rua Válida");
        estacionamentoValido.setCapacidade(0);
        assertThrows(IllegalArgumentException.class, () -> estacionamentoService.criarEstacionamento(estacionamentoValido));
        verify(estacionamentoRepository, never()).save(any(Estacionamento.class));

        estacionamentoValido.setCapacidade(-5);
        assertThrows(IllegalArgumentException.class, () -> estacionamentoService.criarEstacionamento(estacionamentoValido));
        verify(estacionamentoRepository, never()).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando hora de fechamento for anterior à de abertura")
    void deveLancarExcecaoQuandoHoraFechamentoAnteriorAbertura() {
        estacionamentoValido.setEndereco("Rua Válida");
        estacionamentoValido.setHoraAbertura(LocalTime.of(10, 0));
        estacionamentoValido.setHoraFechamento(LocalTime.of(9, 0));
        assertThrows(IllegalArgumentException.class, () -> estacionamentoService.criarEstacionamento(estacionamentoValido));
        verify(estacionamentoRepository, never()).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve retornar estacionamento por ID quando encontrado")
    void deveRetornarEstacionamentoPorIdQuandoEncontrado() {
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamentoValido));

        Estacionamento encontrado = estacionamentoService.buscarEstacionamentoPorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        verify(estacionamentoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar estacionamento por ID inexistente")
    void deveLancarExcecaoQuandoBuscarEstacionamentoPorIdInexistente() {
        when(estacionamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> estacionamentoService.buscarEstacionamentoPorId(99L));
        verify(estacionamentoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todos os estacionamentos")
    void deveListarTodosEstacionamentos() {
        List<Estacionamento> estacionamentos = Arrays.asList(estacionamentoValido, new Estacionamento());
        when(estacionamentoRepository.findAll()).thenReturn(estacionamentos);

        List<Estacionamento> lista = estacionamentoService.listarTodosEstacionamentos();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(estacionamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar um estacionamento existente com sucesso")
    void deveAtualizarEstacionamentoExistente() {
        Contratante contratanteReal = new Contratante(10L, "Contratante Teste", "11.222.333/0001-44", "contrato@teste.com", "tel", new HashSet<>(), new HashSet<>());
        Contratante contratanteSpy = Mockito.spy(contratanteReal);

        Estacionamento estacionamentoExistente = new Estacionamento(1L, "Nome Antigo", "End Antigo", 50, LocalTime.of(8,0), LocalTime.of(18,0), new HashSet<>());
        estacionamentoExistente.addContratante(contratanteSpy); // Associa o spy ao estacionamento existente

        Estacionamento estacionamentoAtualizadoPayload = new Estacionamento();
        estacionamentoAtualizadoPayload.setNome("Nome Atualizado");
        estacionamentoAtualizadoPayload.setEndereco("Endereço Novo");
        estacionamentoAtualizadoPayload.setCapacidade(70);
        estacionamentoAtualizadoPayload.setHoraAbertura(LocalTime.of(9, 0));
        estacionamentoAtualizadoPayload.setHoraFechamento(LocalTime.of(20, 0));
        estacionamentoAtualizadoPayload.setContratantes(new HashSet<>()); // Removendo contratantes existentes

        lenient().when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamentoExistente));
        lenient().when(estacionamentoRepository.findByNome(anyString())).thenReturn(Optional.empty());
        lenient().when(estacionamentoRepository.findByNome(estacionamentoExistente.getNome())).thenReturn(Optional.of(estacionamentoExistente));
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Estacionamento atualizado = estacionamentoService.atualizarEstacionamento(1L, estacionamentoAtualizadoPayload);

        assertNotNull(atualizado);
        assertEquals("Nome Atualizado", atualizado.getNome());
        assertEquals("Endereço Novo", atualizado.getEndereco());
        assertEquals(70, atualizado.getCapacidade());
        assertEquals(LocalTime.of(9, 0), atualizado.getHoraAbertura());
        assertEquals(LocalTime.of(20, 0), atualizado.getHoraFechamento());
        assertTrue(atualizado.getContratantes().isEmpty());

        // Verifica se o método removeEstacionamento foi chamado no SPY do contratante
        verify(contratanteSpy, times(1)).removeEstacionamento(estacionamentoExistente);
        // Verifica o estado da coleção bidirecional no SPY
        assertFalse(contratanteSpy.getEstacionamentos().contains(estacionamentoExistente));

        verify(estacionamentoRepository, times(1)).findById(1L);
        verify(estacionamentoRepository, times(1)).save(estacionamentoExistente);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar estacionamento inexistente")
    void deveLancarExcecaoAoAtualizarEstacionamentoInexistente() {
        Estacionamento estacionamentoAtualizado = new Estacionamento();
        estacionamentoAtualizado.setNome("Nome");
        estacionamentoAtualizado.setEndereco("End");
        estacionamentoAtualizado.setCapacidade(10);
        estacionamentoAtualizado.setHoraAbertura(LocalTime.now());
        estacionamentoAtualizado.setHoraFechamento(LocalTime.now());
        estacionamentoAtualizado.setContratantes(new HashSet<>());

        when(estacionamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> estacionamentoService.atualizarEstacionamento(99L, estacionamentoAtualizado));
        verify(estacionamentoRepository, never()).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar atualizar estacionamento com nome em branco")
    void deveLancarExcecaoAoAtualizarEstacionamentoComNomeEmBranco() {
        Estacionamento estacionamentoAtualizado = new Estacionamento();
        estacionamentoAtualizado.setNome("");
        estacionamentoAtualizado.setEndereco("End");
        estacionamentoAtualizado.setCapacidade(10);
        estacionamentoAtualizado.setHoraAbertura(LocalTime.now());
        estacionamentoAtualizado.setHoraFechamento(LocalTime.now());
        estacionamentoAtualizado.setContratantes(new HashSet<>());

        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamentoValido));

        assertThrows(DescricaoEmBrancoException.class, () -> estacionamentoService.atualizarEstacionamento(1L, estacionamentoAtualizado));
        verify(estacionamentoRepository, never()).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve deletar um estacionamento existente com sucesso e desassociar de contratantes")
    void deveDeletarEstacionamentoExistente() {
        // Crie uma instância REAL de Contratante e um SPY dela
        Contratante contratanteReal = new Contratante();
        contratanteReal.setId(10L);
        contratanteReal.setNome("Contratante Associado Real");
        contratanteReal.setCpfCnpj("00.000.000-00");
        contratanteReal.setEmail("ass@email.com");
        contratanteReal.setEstacionamentos(new HashSet<>());
        contratanteReal.setEventos(new HashSet<>());
        Contratante contratanteSpy = Mockito.spy(contratanteReal);


        Estacionamento estacionamentoParaDeletar = new Estacionamento();
        estacionamentoParaDeletar.setId(1L);
        estacionamentoParaDeletar.setNome("Para Deletar");
        estacionamentoParaDeletar.setEndereco("Rua XYZ");
        estacionamentoParaDeletar.setCapacidade(20);
        estacionamentoParaDeletar.setHoraAbertura(LocalTime.of(8,0));
        estacionamentoParaDeletar.setHoraFechamento(LocalTime.of(18,0));
        estacionamentoParaDeletar.setContratantes(new HashSet<>());

        // Associa o contratanteSpy ao estacionamento e vice-versa para simular o estado inicial
        estacionamentoParaDeletar.addContratante(contratanteSpy);


        lenient().when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamentoParaDeletar));
        doNothing().when(estacionamentoRepository).delete(estacionamentoParaDeletar);

        assertDoesNotThrow(() -> estacionamentoService.deletarEstacionamento(1L));

        verify(estacionamentoRepository, times(1)).findById(1L);
        verify(estacionamentoRepository, times(1)).delete(estacionamentoParaDeletar);
        // Verifica se o método removeEstacionamento foi chamado no SPY
        verify(contratanteSpy, times(1)).removeEstacionamento(estacionamentoParaDeletar);

        // Verifica o estado da coleção no SPY após a execução do serviço
        assertFalse(contratanteSpy.getEstacionamentos().contains(estacionamentoParaDeletar));
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar estacionamento inexistente")
    void deveLancarExcecaoAoDeletarEstacionamentoInexistente() {
        when(estacionamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () -> estacionamentoService.deletarEstacionamento(99L));
        verify(estacionamentoRepository, never()).delete(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao tentar atualizar estacionamento com nome duplicado em outro estacionamento")
    void deveLancarExcecaoAoAtualizarEstacionamentoComNomeDuplicadoEmOutro() {
        Estacionamento outroEstacionamento = new Estacionamento(2L, "Nome Duplicado", "Outro Endereço", 100, LocalTime.now(), LocalTime.now(), new HashSet<>());
        Estacionamento estacionamentoExistente = new Estacionamento(1L, "Original", "End Original", 50, LocalTime.now(), LocalTime.now(), new HashSet<>());

        Estacionamento atualizacao = new Estacionamento();
        atualizacao.setNome("Nome Duplicado");
        atualizacao.setEndereco("Novo Endereço");
        atualizacao.setCapacidade(10);
        atualizacao.setHoraAbertura(LocalTime.of(1,0));
        atualizacao.setHoraFechamento(LocalTime.of(2,0));
        atualizacao.setContratantes(new HashSet<>());

        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamentoExistente));
        when(estacionamentoRepository.findByNome(atualizacao.getNome())).thenReturn(Optional.of(outroEstacionamento));

        assertThrows(IllegalArgumentException.class, () -> estacionamentoService.atualizarEstacionamento(1L, atualizacao));
        verify(estacionamentoRepository, times(1)).findById(1L);
        verify(estacionamentoRepository, times(1)).findByNome(atualizacao.getNome());
        verify(estacionamentoRepository, never()).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao criar estacionamento com nome duplicado")
    void deveLancarExcecaoQuandoCriarEstacionamentoComNomeDuplicado() {
        Estacionamento estacionamentoComDuplicidade = new Estacionamento();
        estacionamentoComDuplicidade.setNome("Estacionamento Teste");
        estacionamentoComDuplicidade.setEndereco("Rua Duplicada, 456");
        estacionamentoComDuplicidade.setCapacidade(10);
        estacionamentoComDuplicidade.setHoraAbertura(LocalTime.of(8,0));
        estacionamentoComDuplicidade.setHoraFechamento(LocalTime.of(18,0));
        estacionamentoComDuplicidade.setContratantes(new HashSet<>());

        when(estacionamentoRepository.findByNome(estacionamentoComDuplicidade.getNome())).thenReturn(Optional.of(new Estacionamento()));

        assertThrows(IllegalArgumentException.class, () -> estacionamentoService.criarEstacionamento(estacionamentoComDuplicidade));
        verify(estacionamentoRepository, never()).save(any(Estacionamento.class));
    }

    @Test
    @DisplayName("Deve permitir atualizar estacionamento com o mesmo nome (não é considerado duplicidade consigo mesmo)")
    void devePermitirAtualizarEstacionamentoComMesmoNome() {
        Estacionamento estacionamentoExistente = new Estacionamento(1L, "Estacionamento Central", "Rua Centro", 150, LocalTime.of(8,0), LocalTime.of(22,0), new HashSet<>());

        Estacionamento atualizacao = new Estacionamento();
        atualizacao.setNome("Estacionamento Central");
        atualizacao.setEndereco("Rua Centro Atualizada");
        atualizacao.setCapacidade(160);
        atualizacao.setHoraAbertura(LocalTime.of(8,0));
        atualizacao.setHoraFechamento(LocalTime.of(22,0));
        atualizacao.setContratantes(new HashSet<>());

        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamentoExistente));
        when(estacionamentoRepository.findByNome(atualizacao.getNome())).thenReturn(Optional.of(estacionamentoExistente));
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenReturn(estacionamentoExistente);

        Estacionamento atualizado = estacionamentoService.atualizarEstacionamento(1L, atualizacao);

        assertNotNull(atualizado);
        assertEquals("Estacionamento Central", atualizado.getNome());
        assertEquals("Rua Centro Atualizada", atualizado.getEndereco());
        assertEquals(160, atualizado.getCapacidade());
        verify(estacionamentoRepository, times(1)).findById(1L);
        verify(estacionamentoRepository, times(1)).findByNome(atualizacao.getNome());
        verify(estacionamentoRepository, times(1)).save(estacionamentoExistente);
    }
}