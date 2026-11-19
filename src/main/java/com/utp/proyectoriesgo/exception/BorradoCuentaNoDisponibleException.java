package com.utp.proyectoriesgo.exception;

public class BorradoCuentaNoDisponibleException extends RuntimeException {
    public BorradoCuentaNoDisponibleException() {
        super("La eliminación de cuenta aún no está disponible.");
    }
}
