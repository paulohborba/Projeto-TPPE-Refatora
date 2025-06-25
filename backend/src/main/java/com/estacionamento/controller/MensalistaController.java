package com.estacionamento.controller;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Mensalista;
import com.estacionamento.service.MensalistaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensalistas")
public class MensalistaController {

    private final MensalistaService mensalistaService;

    public MensalistaController(MensalistaService mensalistaService) {
        this.mensalistaService = mensalistaService;
    }

    @PostMapping
    public ResponseEntity<Mensalista> criarMensalista(@RequestBody Mensalista mensalista) {
        Mensalista novoMensalista = mensalistaService.criarMensalista(mensalista);
        return new ResponseEntity<>(novoMensalista, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mensalista> buscarMensalistaPorId(@PathVariable Long id) {
        Mensalista mensalista = mensalistaService.buscarMensalistaPorId(id);
        return new ResponseEntity<>(mensalista, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Mensalista>> listarTodosMensalistas() {
        List<Mensalista> mensalistas = mensalistaService.listarTodosMensalistas();
        return new ResponseEntity<>(mensalistas, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mensalista> atualizarMensalista(@PathVariable Long id, @RequestBody Mensalista mensalistaAtualizado) {
        Mensalista mensalista = mensalistaService.atualizarMensalista(id, mensalistaAtualizado);
        return new ResponseEntity<>(mensalista, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMensalista(@PathVariable Long id) {
        mensalistaService.deletarMensalista(id);
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