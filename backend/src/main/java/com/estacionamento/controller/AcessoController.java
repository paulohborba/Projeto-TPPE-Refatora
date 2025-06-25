package com.estacionamento.controller;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.exception.ValorAcessoInvalidoException;
import com.estacionamento.model.Acesso;
import com.estacionamento.service.AcessoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acessos")
public class AcessoController {

    private final AcessoService acessoService;

    public AcessoController(AcessoService acessoService) {
        this.acessoService = acessoService;
    }

    @PostMapping
    public ResponseEntity<Acesso> criarAcesso(@RequestBody Acesso acesso) {
        Acesso novoAcesso = acessoService.criarAcesso(acesso);
        return new ResponseEntity<>(novoAcesso, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Acesso> buscarAcessoPorId(@PathVariable Long id) {
        Acesso acesso = acessoService.buscarAcessoPorId(id);
        return new ResponseEntity<>(acesso, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Acesso>> listarTodosAcessos() {
        List<Acesso> acessos = acessoService.listarTodosAcessos();
        return new ResponseEntity<>(acessos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Acesso> atualizarAcesso(@PathVariable Long id, @RequestBody Acesso acessoAtualizado) {
        Acesso acesso = acessoService.atualizarAcesso(id, acessoAtualizado);
        return new ResponseEntity<>(acesso, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAcesso(@PathVariable Long id) {
        acessoService.deletarAcesso(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({DescricaoEmBrancoException.class, IllegalArgumentException.class, ValorAcessoInvalidoException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ObjetoNaoEncontradoException.class})
    public ResponseEntity<String> handleNotFound(ObjetoNaoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}