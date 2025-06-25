package com.estacionamento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValorAcessoInvalidoException extends RuntimeException {
    public ValorAcessoInvalidoException(String message) {
        super(message);
    }
}