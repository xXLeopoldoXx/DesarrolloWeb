package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.RespuestaItem;
import com.utp.proyectoriesgo.dto.ResultadoResponse;
import com.utp.proyectoriesgo.model.Factor;
import com.utp.proyectoriesgo.model.Respuesta;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraAutoevaluacionTest {
    private final CalculadoraAutoevaluacion calculadora = new CalculadoraAutoevaluacion();

    /** Las 9 respuestas: las de {@code especiales} y «No» en el resto. */
    private static List<RespuestaItem> respuestas(Map<Factor, Respuesta> especiales) {
        List<RespuestaItem> items = new ArrayList<>();
        for (Factor factor : Factor.values()) {
            items.add(new RespuestaItem(factor, especiales.getOrDefault(factor, Respuesta.NO)));
        }
        return items;
    }

    @Test
    void sinNingunSi_elNivelEsBajoYNoHayAlerta() {
        ResultadoResponse r = calculadora.calcular(respuestas(Map.of()));

        assertThat(r.puntaje()).isZero();
        assertThat(r.nivel()).isEqualTo("BAJO");
        assertThat(r.alertaCritica()).isFalse();
        assertThat(r.senales()).isEmpty();
        assertThat(r.porConfirmar()).isEmpty();
    }

    @Test
    void cadaSiSumaElPesoDeSuFactorYApareceComoSenal() {
        ResultadoResponse r = calculadora.calcular(respuestas(Map.of(
                Factor.ACOSO_O_PERSECUCION, Respuesta.SI,
                Factor.CONTROL_EXTREMO, Respuesta.SI)));

        assertThat(r.puntaje()).isEqualTo(25);
        assertThat(r.nivel()).isEqualTo("BAJO");
        assertThat(r.senales()).containsExactlyInAnyOrder("Acoso o persecución", "Control extremo");
    }

    @Test
    void noEstoySeguraNoSumaYQuedaPorConfirmar() {
        ResultadoResponse r = calculadora.calcular(respuestas(Map.of(
                Factor.ACCESO_ARMAS, Respuesta.NO_SEGURA,
                Factor.DENUNCIAS_PREVIAS, Respuesta.SI)));

        assertThat(r.puntaje()).isEqualTo(10);
        assertThat(r.porConfirmar()).containsExactly("Acceso a armas");
        assertThat(r.senales()).containsExactly("Denuncias previas");
    }

    @Test
    void losUmbralesDelNivelCoincidenConRiesgoCalculador() {
        // 31 → MEDIO, 61 → ALTO, 81 → MUY_ALTO (misma regla que RiesgoCalculador).
        assertThat(nivelCon(Factor.AMENAZA_MUERTE, Factor.CONTROL_EXTREMO, Factor.CONSUMO_ALCOHOL_DROGAS_AGRESOR))
                .isEqualTo("MEDIO"); // 25+10+5 = 40
        assertThat(nivelCon(Factor.AMENAZA_MUERTE, Factor.ACCESO_ARMAS, Factor.DENUNCIAS_PREVIAS,
                Factor.CONTROL_EXTREMO, Factor.SEPARACION_RECIENTE)).isEqualTo("ALTO"); // 75
        assertThat(nivelCon(Factor.values())).isEqualTo("MUY_ALTO"); // 140
    }

    @Test
    void amenazaYEstrangulamientoActivanLaAlertaCriticaYSubenAAlto() {
        ResultadoResponse r = calculadora.calcular(respuestas(Map.of(
                Factor.AMENAZA_MUERTE, Respuesta.SI,
                Factor.ESTRANGULAMIENTO_PREVIO, Respuesta.SI)));

        assertThat(r.puntaje()).isEqualTo(55); // por puntaje sería MEDIO
        assertThat(r.alertaCritica()).isTrue();
        assertThat(r.nivel()).isEqualTo("ALTO");
    }

    @Test
    void incumplimientoYArmasTambienActivanLaAlertaCritica() {
        ResultadoResponse r = calculadora.calcular(respuestas(Map.of(
                Factor.INCUMPLIMIENTO_MEDIDA_PROTECCION, Respuesta.SI,
                Factor.ACCESO_ARMAS, Respuesta.SI)));

        assertThat(r.alertaCritica()).isTrue();
        assertThat(r.nivel()).isEqualTo("ALTO"); // 35 por puntaje = MEDIO
    }

    @Test
    void unaDeLasDosCondicionesSolaNoEsAlertaCritica() {
        ResultadoResponse r = calculadora.calcular(respuestas(Map.of(Factor.AMENAZA_MUERTE, Respuesta.SI)));

        assertThat(r.alertaCritica()).isFalse();
    }

    @Test
    void unaRespuestaNoEstoySeguraNoCuentaParaLaAlertaCritica() {
        ResultadoResponse r = calculadora.calcular(respuestas(Map.of(
                Factor.AMENAZA_MUERTE, Respuesta.SI,
                Factor.ESTRANGULAMIENTO_PREVIO, Respuesta.NO_SEGURA)));

        assertThat(r.alertaCritica()).isFalse();
        assertThat(r.porConfirmar()).containsExactly("Estrangulamiento previo");
    }

    private String nivelCon(Factor... conSi) {
        Map<Factor, Respuesta> mapa = new java.util.EnumMap<>(Factor.class);
        for (Factor factor : conSi) {
            mapa.put(factor, Respuesta.SI);
        }
        return calculadora.calcular(respuestas(mapa)).nivel();
    }
}
