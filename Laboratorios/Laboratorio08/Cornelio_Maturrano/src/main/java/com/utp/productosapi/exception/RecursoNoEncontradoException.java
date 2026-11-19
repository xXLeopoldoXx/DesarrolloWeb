package com.utp.productosapi.exception;

public class RecursoNoEncontradoException
        extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}