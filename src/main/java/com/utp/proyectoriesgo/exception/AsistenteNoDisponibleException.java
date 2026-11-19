package com.utp.proyectoriesgo.exception;

/**
 * El asistente no pudo responder (sin clave, Gemini caído, cuota agotada, tiempo de espera...).
 * El mensaje se muestra a la usuaria, así que NUNCA debe incluir detalles técnicos ni la clave.
 */
public class AsistenteNoDisponibleException extends RuntimeException {

    public AsistenteNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
