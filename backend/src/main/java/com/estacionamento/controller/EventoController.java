package com.estacionamento.controller;

import com.estacionamento.exception.DescricaoEmBrancoException;
import com.estacionamento.exception.ObjetoNaoEncontradoException;
import com.estacionamento.model.Evento;
import com.estacionamento.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<Evento> criarEvento(@RequestBody Evento evento) {
        Evento novoEvento = eventoService.criarEvento(evento);
        return new ResponseEntity<>(novoEvento, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarEventoPorId(@PathVariable Long id) {
        Evento evento = eventoService.buscarEventoPorId(id);
        return new ResponseEntity<>(evento, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarTodosEventos() {
        List<Evento> eventos = eventoService.listarTodosEventos();
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizarEvento(@PathVariable Long id, @RequestBody Evento eventoAtualizado) {
        Evento evento = eventoService.atualizarEvento(id, eventoAtualizado);
        return new ResponseEntity<>(evento, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEvento(@PathVariable Long id) {
        eventoService.deletarEvento(id);
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