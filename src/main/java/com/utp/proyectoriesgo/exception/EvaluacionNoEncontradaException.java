package com.utp.proyectoriesgo.exception;

public class EvaluacionNoEncontradaException extends RuntimeException {
    public EvaluacionNoEncontradaException(Long id) {
        super("No existe una evaluación con id: " + id);
    }
}