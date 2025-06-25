package com.estacionamento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DescricaoEmBrancoException extends RuntimeException {
    public DescricaoEmBrancoException(String message) {
        super(message);
    }
}