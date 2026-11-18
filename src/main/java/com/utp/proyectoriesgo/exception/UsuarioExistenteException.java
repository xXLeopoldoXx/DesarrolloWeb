package com.utp.proyectoriesgo.exception;

public class UsuarioExistenteException extends RuntimeException {

    public UsuarioExistenteException(String mensaje) {
        super(mensaje);
    }
}
