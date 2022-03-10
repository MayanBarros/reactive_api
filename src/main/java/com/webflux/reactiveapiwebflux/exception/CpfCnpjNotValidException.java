package com.webflux.reactiveapiwebflux.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CpfCnpjNotValidException extends RuntimeException {
    public CpfCnpjNotValidException(String message) {
        super(message);
    }
}
