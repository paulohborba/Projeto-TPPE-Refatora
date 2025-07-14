package com.estacionamento_tppe.service;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Veiculo;
import com.estacionamento.repository.VeiculoRepository;
import com.estacionamento.service.VeiculoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    private Veiculo veiculoValido;
    private Veiculo veiculoInvalidoPlacaBranco;
    private Veiculo veiculoComPlacaExistente;

    @BeforeEach
    void setUp() {
        veiculoValido = new Veiculo();
        veiculoValido.setId(1L);
        veiculoValido.setPlaca("ABC1234");
        veiculoValido.setMarca("Fiat");
        veiculoValido.setModelo("Palio");
        veiculoValido.setCor("Preto");

        veiculoInvalidoPlacaBranco = new Veiculo();
        veiculoInvalidoPlacaBranco.setPlaca("");
        veiculoInvalidoPlacaBranco.setMarca("Ford");
        veiculoInvalidoPlacaBranco.setModelo("Ka");
        veiculoInvalidoPlacaBranco.setCor("Branco");

        veiculoComPlacaExistente = new Veiculo();
        veiculoComPlacaExistente.setPlaca("XYZ7890");
        veiculoComPlacaExistente.setMarca("VW");
        veiculoComPlacaExistente.setModelo("Gol");
        veiculoComPlacaExistente.setCor("Azul");
    }

    @Test
    @DisplayName("Deve criar um veículo válido com sucesso")
    void deveCriarVeiculoValido() {
        when(veiculoRepository.findByPlaca(veiculoValido.getPlaca())).thenReturn(Optional.empty());
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculoValido);

        Veiculo salvo = veiculoService.criarVeiculo(veiculoValido);

        assertNotNull(salvo);
        assertEquals("ABC1234", salvo.getPlaca());
        verify(veiculoRepository, times(1)).findByPlaca(veiculoValido.getPlaca()); 
        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar criar veículo com placa em branco")
    void deveLancarExcecaoQuandoCriarVeiculoComPlacaEmBranco() {
        assertThrows(DescricaoEmBrancoException.class, () ->
            veiculoService.criarVeiculo(veiculoInvalidoPlacaBranco)
        );
        verify(veiculoRepository, never()).findByPlaca(anyString());
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao tentar criar veículo com placa já existente")
    void deveLancarExcecaoQuandoCriarVeiculoComPlacaExistente() {
        when(veiculoRepository.findByPlaca(veiculoComPlacaExistente.getPlaca())).thenReturn(
            Optional.of(new Veiculo()));

        assertThrows(IllegalArgumentException.class, () ->
            veiculoService.criarVeiculo(veiculoComPlacaExistente)
        );
        verify(veiculoRepository, times(1)).findByPlaca(veiculoComPlacaExistente.getPlaca());
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve retornar veículo por ID quando encontrado")
    void deveRetornarVeiculoPorIdQuandoEncontrado() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoValido));

        Veiculo encontrado = veiculoService.buscarVeiculoPorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        assertEquals("ABC1234", encontrado.getPlaca());
        verify(veiculoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar veículo por ID inexistente")
    void deveLancarExcecaoQuandoBuscarVeiculoPorIdInexistente() {
        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ObjetoNaoEncontradoException.class, () ->
            veiculoService.buscarVeiculoPorId(99L)
        );
        verify(veiculoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todos os veículos")
    void deveListarTodosVeiculos() {
        List<Veiculo> veiculos = Arrays.asList(veiculoValido, veiculoComPlacaExistente);
        when(veiculoRepository.findAll()).thenReturn(veiculos);

        List<Veiculo> lista = veiculoService.listarTodosVeiculos();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(veiculoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar um veículo existente com sucesso")
    void deveAtualizarVeiculoExistente() {
        Veiculo atualizacao = new Veiculo();
        atualizacao.setPlaca("DEF5678");
        atualizacao.setMarca("Toyota");
        atualizacao.setModelo("Corolla");
        atualizacao.setCor("Prata");

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoValido));
        when(veiculoRepository.findByPlaca(atualizacao.getPlaca())).thenReturn(Optional.empty());
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculoValido);

        Veiculo atualizado = veiculoService.atualizarVeiculo(1L, atualizacao);

        assertNotNull(atualizado);
        assertEquals("DEF5678", atualizado.getPlaca());
        assertEquals("Toyota", atualizado.getMarca());
        assertEquals("Corolla", atualizado.getModelo());
        assertEquals("Prata", atualizado.getCor());
        verify(veiculoRepository, times(1)).findById(1L);
        verify(veiculoRepository, times(1)).findByPlaca(atualizacao.getPlaca());
        verify(veiculoRepository, times(1)).save(veiculoValido);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar veículo inexistente")
    void deveLancarExcecaoAoAtualizarVeiculoInexistente() {
        Veiculo atualizacao = new Veiculo();
        atualizacao.setPlaca("XXX1111");

        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class, () ->
            veiculoService.atualizarVeiculo(99L, atualizacao)
        );
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar DescricaoEmBrancoException ao tentar atualizar veículo com placa em branco")
    void deveLancarExcecaoAoAtualizarVeiculoComPlacaEmBranco() {
        Veiculo atualizacao = new Veiculo();
        atualizacao.setPlaca("");

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoValido));

        assertThrows(DescricaoEmBrancoException.class, () ->
            veiculoService.atualizarVeiculo(1L, atualizacao)
        );
        verify(veiculoRepository, never()).findByPlaca(anyString());
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName(
        "Deve lançar IllegalArgumentException ao tentar atualizar veículo com placa já existente em outro veículo"
    )
    void deveLancarExcecaoAoAtualizarVeiculoComPlacaDuplicada() {
        Veiculo outroVeiculo = new Veiculo();
        outroVeiculo.setId(2L);
        outroVeiculo.setPlaca("DEF5678");

        Veiculo atualizacao = new Veiculo();
        atualizacao.setPlaca("DEF5678");
        atualizacao.setMarca("BMW");
        atualizacao.setModelo("X1");
        atualizacao.setCor("Branco");

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoValido));
        when(veiculoRepository.findByPlaca(atualizacao.getPlaca())).thenReturn(Optional.of(outroVeiculo));

        assertThrows(IllegalArgumentException.class, () ->
            veiculoService.atualizarVeiculo(1L, atualizacao)
        );
        verify(veiculoRepository, times(1)).findById(1L);
        verify(veiculoRepository, times(1)).findByPlaca(atualizacao.getPlaca());
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve permitir atualizar veículo com a mesma placa(ou seja, não conta como duplicidade com ele mesmo)")
    void devePermitirAtualizarVeiculoComMesmaPlaca() {
        Veiculo atualizacao = new Veiculo();
        atualizacao.setPlaca("ABC1234");
        atualizacao.setMarca("Nova Marca");
        atualizacao.setModelo("Novo Modelo");
        atualizacao.setCor("Nova Cor");

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoValido));
        when(veiculoRepository.findByPlaca(atualizacao.getPlaca())).thenReturn(Optional.of(veiculoValido));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculoValido);

        Veiculo atualizado = veiculoService.atualizarVeiculo(1L, atualizacao);

        assertNotNull(atualizado);
        assertEquals("ABC1234", atualizado.getPlaca());
        assertEquals("Nova Marca", atualizado.getMarca());
        verify(veiculoRepository, times(1)).findById(1L);
        verify(veiculoRepository, times(1)).findByPlaca(atualizacao.getPlaca());
        verify(veiculoRepository, times(1)).save(veiculoValido);
    }

    @Test
    @DisplayName("Deve deletar um veículo existente com sucesso")
    void deveDeletarVeiculoExistente() {
        when(veiculoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(veiculoRepository).deleteById(1L);

        assertDoesNotThrow(() -> veiculoService.deletarVeiculo(1L));

        verify(veiculoRepository, times(1)).existsById(1L);
        verify(veiculoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar veículo inexistente")
    void deveLancarExcecaoAoDeletarVeiculoInexistente() {
        when(veiculoRepository.existsById(99L)).thenReturn(false);

        assertThrows(ObjetoNaoEncontradoException.class, () ->
            veiculoService.deletarVeiculo(99L)
        );
        verify(veiculoRepository, times(1)).existsById(99L);
        verify(veiculoRepository, never()).deleteById(anyLong());
    }
}