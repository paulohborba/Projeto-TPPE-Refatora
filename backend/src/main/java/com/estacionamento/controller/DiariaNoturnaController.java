package com.estacionamento.controller;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.DiariaNoturna;
import com.estacionamento.service.DiariaNoturnaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diarias-noturnas")
public class DiariaNoturnaController {

    private final DiariaNoturnaService diariaNoturnaService;

    public DiariaNoturnaController(DiariaNoturnaService diariaNoturnaService) {
        this.diariaNoturnaService = diariaNoturnaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiariaNoturna> buscarDiariaNoturnaPorId(@PathVariable Long id) {
        DiariaNoturna diariaNoturna = diariaNoturnaService.buscarDiariaNoturnaPorId(id);
        return new ResponseEntity<>(diariaNoturna, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DiariaNoturna>> listarTodasDiariasNoturnas() {
        List<DiariaNoturna> diariasNoturnas = diariaNoturnaService.listarTodasDiariasNoturnas();
        return new ResponseEntity<>(diariasNoturnas, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiariaNoturna> atualizarDiariaNoturna(
        @PathVariable Long id, @RequestBody DiariaNoturna diariaNoturnaAtualizada
    ) {
        DiariaNoturna diariaNoturna = diariaNoturnaService.atualizarDiariaNoturna(id, diariaNoturnaAtualizada);
        return new ResponseEntity<>(diariaNoturna, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDiariaNoturna(@PathVariable Long id) {
        diariaNoturnaService.deletarDiariaNoturna(id);
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



