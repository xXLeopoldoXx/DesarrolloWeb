package com.utp.semana2.exception;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(Long id) {
        // Modificación del mensaje de error
        super("El producto con ID " + id + " no se encuentra registrado en el sistema.");
    }
}