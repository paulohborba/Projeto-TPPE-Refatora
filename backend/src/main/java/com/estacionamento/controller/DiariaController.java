package com.estacionamento.controller;


import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Diaria;
import com.estacionamento.service.DiariaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diarias")
public class DiariaController {

    private final DiariaService diariaService;

    public DiariaController(DiariaService diariaService) {
        this.diariaService = diariaService;
    }

    @PostMapping
    public ResponseEntity<Diaria> criarDiaria(@RequestBody Diaria diaria) {
        Diaria novaDiaria = diariaService.criarDiaria(diaria);
        return new ResponseEntity<>(novaDiaria, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diaria> buscarDiariaPorId(@PathVariable Long id) {
        Diaria diaria = diariaService.buscarDiariaPorId(id);
        return new ResponseEntity<>(diaria, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Diaria>> listarTodasDiarias() {
        List<Diaria> diarias = diariaService.listarTodasDiarias();
        return new ResponseEntity<>(diarias, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diaria> atualizarDiaria(@PathVariable Long id, @RequestBody Diaria diariaAtualizada) {
        Diaria diaria = diariaService.atualizarDiaria(id, diariaAtualizada);
        return new ResponseEntity<>(diaria, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDiaria(@PathVariable Long id) {
        diariaService.deletarDiaria(id);
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