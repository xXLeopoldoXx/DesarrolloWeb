package com.utp.proyectoriesgo.exception;

/** La usuaria superó el máximo de mensajes al asistente por hora (se responde 429). */
public class LimiteMensajesException extends RuntimeException {

    public LimiteMensajesException(String mensaje) {
        super(mensaje);
    }
}
