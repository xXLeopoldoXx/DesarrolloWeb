package com.utp.productosapi.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    // ERROR 404: PRODUCTO NO ENCONTRADO
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(
            RecursoNoEncontradoException ex) {

        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ERROR 400: REGLA DE NEGOCIO
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> reglaNegocio(
            ReglaNegocioException ex) {

        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ERROR 500: ERROR SIMULADO PARA ROLLBACK
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> errorSimulado(
            IllegalStateException ex) {

        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
    }

    // CONSTRUIR RESPUESTA JSON
    private ResponseEntity<Map<String, Object>> respuesta(
            HttpStatus status, String mensaje) {

        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", mensaje
        ));
    }
}