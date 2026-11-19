package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.LimiteMensajesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Límite de mensajes por usuaria en una ventana de tiempo (por defecto 20 por hora).
 * Protege la cuota gratuita de Gemini. Es en memoria: se reinicia al apagar el servidor.
 */
@Component
public class LimitadorMensajes {

    private final int maximo;
    private final Duration ventana;
    private final Clock reloj;
    private final Map<String, Deque<Instant>> envios = new ConcurrentHashMap<>();

    @Autowired
    public LimitadorMensajes(@Value("${app.asistente.max-mensajes-hora:20}") int maximo) {
        this(maximo, Duration.ofHours(1), Clock.systemUTC());
    }

    // Para pruebas: permite controlar el reloj
    LimitadorMensajes(int maximo, Duration ventana, Clock reloj) {
        this.maximo = maximo;
        this.ventana = ventana;
        this.reloj = reloj;
    }

    /** Registra un envío de la usuaria. Lanza LimiteMensajesException si ya llegó al máximo. */
    public void registrar(String usuario) {
        Instant ahora = reloj.instant();
        Deque<Instant> cola = envios.computeIfAbsent(usuario, clave -> new ArrayDeque<>());

        synchronized (cola) {
            Instant limite = ahora.minus(ventana);
            while (!cola.isEmpty() && cola.peekFirst().isBefore(limite)) {
                cola.pollFirst();
            }
            if (cola.size() >= maximo) {
                long minutos = Duration.between(ahora, cola.peekFirst().plus(ventana)).toMinutes() + 1;
                throw new LimiteMensajesException(
                        "Has enviado muchos mensajes seguidos. Inténtalo de nuevo en unos " + minutos + " minutos.");
            }
            cola.addLast(ahora);
        }
    }
}
