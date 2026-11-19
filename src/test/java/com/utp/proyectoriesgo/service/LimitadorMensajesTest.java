package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.LimiteMensajesException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimitadorMensajesTest {

    /** Reloj que avanza solo cuando la prueba lo indica. */
    static class RelojManual extends Clock {
        private Instant ahora = Instant.parse("2026-11-18T10:00:00Z");

        void avanzar(Duration duracion) {
            ahora = ahora.plus(duracion);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return ahora;
        }
    }

    @Test
    void permiteHastaElMaximoYBloqueaElSiguiente() {
        var limitador = new LimitadorMensajes(3, Duration.ofHours(1), new RelojManual());

        assertDoesNotThrow(() -> {
            limitador.registrar("maria.l");
            limitador.registrar("maria.l");
            limitador.registrar("maria.l");
        });
        var error = assertThrows(LimiteMensajesException.class, () -> limitador.registrar("maria.l"));
        assertTrue(error.getMessage().contains("minutos"));
    }

    @Test
    void cadaUsuariaTieneSuPropioLimite() {
        var limitador = new LimitadorMensajes(1, Duration.ofHours(1), new RelojManual());

        limitador.registrar("maria.l");

        assertDoesNotThrow(() -> limitador.registrar("otra.usuaria"));
        assertThrows(LimiteMensajesException.class, () -> limitador.registrar("maria.l"));
    }

    @Test
    void despuesDeLaVentanaSePuedeVolverAEnviar() {
        var reloj = new RelojManual();
        var limitador = new LimitadorMensajes(1, Duration.ofHours(1), reloj);

        limitador.registrar("maria.l");
        assertThrows(LimiteMensajesException.class, () -> limitador.registrar("maria.l"));

        reloj.avanzar(Duration.ofMinutes(61));

        assertDoesNotThrow(() -> limitador.registrar("maria.l"));
    }
}
