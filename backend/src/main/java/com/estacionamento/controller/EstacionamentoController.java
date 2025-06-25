package com.estacionamento.controller;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Estacionamento;
import com.estacionamento.service.EstacionamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estacionamentos")
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;

    public EstacionamentoController(EstacionamentoService estacionamentoService) {
        this.estacionamentoService = estacionamentoService;
    }

    @PostMapping
    public ResponseEntity<Estacionamento> criarEstacionamento(@RequestBody Estacionamento estacionamento) {
        Estacionamento novoEstacionamento = estacionamentoService.criarEstacionamento(estacionamento);
        return new ResponseEntity<>(novoEstacionamento, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estacionamento> buscarEstacionamentoPorId(@PathVariable Long id) {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoPorId(id);
        return new ResponseEntity<>(estacionamento, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Estacionamento>> listarTodosEstacionamentos() {
        List<Estacionamento> estacionamentos = estacionamentoService.listarTodosEstacionamentos();
        return new ResponseEntity<>(estacionamentos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estacionamento> atualizarEstacionamento(@PathVariable Long id, @RequestBody Estacionamento estacionamentoAtualizado) {
        Estacionamento estacionamento = estacionamentoService.atualizarEstacionamento(id, estacionamentoAtualizado);
        return new ResponseEntity<>(estacionamento, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEstacionamento(@PathVariable Long id) {
        estacionamentoService.deletarEstacionamento(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({DescricaoEmBrancoException.class})
    public ResponseEntity<String> handleBadRequestException(DescricaoEmBrancoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ObjetoNaoEncontradoException.class})
    public ResponseEntity<String> handleNotFoundException(ObjetoNaoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}
