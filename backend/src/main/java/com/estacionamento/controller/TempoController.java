package com.estacionamento.controller;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Tempo;
import com.estacionamento.service.TempoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tempos")
public class TempoController {

    private final TempoService tempoService;

    public TempoController(TempoService tempoService) {
        this.tempoService = tempoService;
    }

    @PostMapping
    public ResponseEntity<Tempo> criarTempo(@RequestBody Tempo tempo) {
        Tempo novoTempo = tempoService.criarTempo(tempo);
        return new ResponseEntity<>(novoTempo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tempo> buscarTempoPorId(@PathVariable Long id) {
        Tempo tempo = tempoService.buscarTempoPorId(id);
        return new ResponseEntity<>(tempo, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Tempo>> listarTodosTempos() {
        List<Tempo> tempos = tempoService.listarTodosTempos();
        return new ResponseEntity<>(tempos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tempo> atualizarTempo(@PathVariable Long id, @RequestBody Tempo tempoAtualizado) {
        Tempo tempo = tempoService.atualizarTempo(id, tempoAtualizado);
        return new ResponseEntity<>(tempo, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTempo(@PathVariable Long id) {
        tempoService.deletarTempo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({DescricaoEmBrancoException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ObjetoNaoEncontradoException.class})
    public ResponseEntity<String> handleNotFound(ObjetoNaoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}