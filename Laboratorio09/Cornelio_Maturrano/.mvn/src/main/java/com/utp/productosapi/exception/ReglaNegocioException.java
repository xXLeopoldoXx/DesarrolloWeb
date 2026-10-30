package com.utp.productosapi.exception;

public class ReglaNegocioException
        extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}