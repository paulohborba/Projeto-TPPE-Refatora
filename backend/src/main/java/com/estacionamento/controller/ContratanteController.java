package com.estacionamento.controller;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Contratante;
import com.estacionamento.service.ContratanteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contratantes")
public class ContratanteController {

    private final ContratanteService contratanteService;

    public ContratanteController(ContratanteService contratanteService) {
        this.contratanteService = contratanteService;
    }

    @PostMapping
    public ResponseEntity<Contratante> criarContratante(@RequestBody Contratante contratante) {
        Contratante novoContratante = contratanteService.criarContratante(contratante);
        return new ResponseEntity<>(novoContratante, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contratante> buscarContratantePorId(@PathVariable Long id) {
        Contratante contratante = contratanteService.buscarContratantePorId(id);
        return new ResponseEntity<>(contratante, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Contratante>> listarTodosContratantes() {
        List<Contratante> contratantes = contratanteService.listarTodosContratantes();
        return new ResponseEntity<>(contratantes, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contratante> atualizarContratante(
        @PathVariable Long id, @RequestBody Contratante contratanteAtualizado
    ) {
        Contratante contratante = contratanteService.atualizarContratante(id, contratanteAtualizado);
        return new ResponseEntity<>(contratante, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarContratante(@PathVariable Long id) {
        contratanteService.deletarContratante(id);
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