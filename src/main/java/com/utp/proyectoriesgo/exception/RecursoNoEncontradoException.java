package com.utp.proyectoriesgo.exception;

/**
 * Recurso inexistente O que no pertenece a la usuaria autenticada.
 * Se responde 404 en ambos casos a propósito: así no se revela que el recurso existe.
 * Úsala en todos los módulos "/api/mi/**" y "/api/comunidad/**".
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
