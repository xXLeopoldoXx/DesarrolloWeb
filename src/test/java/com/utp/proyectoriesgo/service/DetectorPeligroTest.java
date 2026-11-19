package com.utp.proyectoriesgo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DetectorPeligroTest {

    private final DetectorPeligro detector = new DetectorPeligro();

    @ParameterizedTest
    @ValueSource(strings = {
            "Mi pareja me va a matar",
            "ME QUIERE MATAR!!",
            "me   amenazó   de muerte",            // espacios y tilde
            "Él tiene un arma en la casa",
            "me está golpeando ahora",
            "Estoy en peligro",
            "quiero morir",
            "he pensado en el suicidio",
            "ya no quiero vivir",
            "Dice que si lo dejo me va a hacer algo grave",
            "¿Cuál es la probabilidad de que mi pareja me mate?",
            "Tengo miedo de que me mate",
            "Me va a pegar cuando llegue"
    })
    void detectaFrasesDePeligro(String mensaje) {
        assertTrue(detector.indicaPeligro(mensaje), mensaje);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "¿Qué es la violencia psicológica?",
            "¿Cómo armo un plan de seguridad?",
            "Quiero entender mis resultados",
            "¿Qué hace la Línea 100?",
            "Hola, buenas tardes"
    })
    void noMarcaPreguntasNormales(String mensaje) {
        assertFalse(detector.indicaPeligro(mensaje), mensaje);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void vacioONuloNoEsPeligro(String mensaje) {
        assertFalse(detector.indicaPeligro(mensaje));
    }

    @Test
    void ignoraTildesYMayusculas() {
        assertTrue(detector.indicaPeligro("ME AMENAZÓ DE MUERTE"));
    }
}
