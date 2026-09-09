package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.model.Evaluacion;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RiesgoCalculadorTest {

    private final RiesgoCalculador calculador = new RiesgoCalculador();

    private Evaluacion evaluacionBase() {
        Evaluacion e = new Evaluacion();
        e.setSeudonimoVictima("Caso-Test");
        e.setEdadVictima(30);
        e.setRelacionAgresor("Pareja");
        return e;
    }

    @Test
    void sinFactores_debeSerRiesgoBajo() {
        Evaluacion e = evaluacionBase();

        calculador.calcular(e);

        assertThat(e.getPuntajeRiesgo()).isEqualTo(0);
        assertThat(e.getNivelRiesgo()).isEqualTo("BAJO");
        assertThat(e.isAlertaCritica()).isFalse();
    }

    @Test
    void conAmenazaYArmas_debeSerRiesgoMedio() {
        Evaluacion e = evaluacionBase();
        e.setAmenazaMuerte(true);   // +25
        e.setAccesoArmas(true);     // +20

        calculador.calcular(e);

        assertThat(e.getPuntajeRiesgo()).isEqualTo(45);
        assertThat(e.getNivelRiesgo()).isEqualTo("MEDIO");
    }

    @Test
    void conVariosFactoresGraves_debeSerRiesgoAlto() {
        Evaluacion e = evaluacionBase();
        e.setAmenazaMuerte(true);                      // +25
        e.setEstrangulamientoPrevio(true);             // +30
        e.setIncumplimientoMedidaProteccion(true);      // +15

        calculador.calcular(e);

        assertThat(e.getPuntajeRiesgo()).isEqualTo(70);
        assertThat(e.getNivelRiesgo()).isEqualTo("ALTO");
    }

    @Test
    void conTodosLosFactores_debeSerRiesgoMuyAlto() {
        Evaluacion e = evaluacionBase();
        e.setAmenazaMuerte(true);
        e.setAccesoArmas(true);
        e.setEstrangulamientoPrevio(true);
        e.setDenunciasPrevias(true);
        e.setIncumplimientoMedidaProteccion(true);
        e.setAcosoOPersecucion(true);
        e.setSeparacionReciente(true);
        e.setControlExtremo(true);
        e.setConsumoAlcoholDrogasAgresor(true);

        calculador.calcular(e);

        assertThat(e.getNivelRiesgo()).isEqualTo("MUY_ALTO");
    }

    @Test
    void amenazaMasEstrangulamiento_debeActivarAlertaCritica() {
        Evaluacion e = evaluacionBase();
        e.setAmenazaMuerte(true);
        e.setEstrangulamientoPrevio(true);

        calculador.calcular(e);

        assertThat(e.isAlertaCritica()).isTrue();
        assertThat(e.getNivelRiesgo()).isEqualTo("ALTO"); // forzado, aunque el puntaje solo dé 55
    }

    @Test
    void incumplimientoMasArmas_debeActivarAlertaCritica() {
        Evaluacion e = evaluacionBase();
        e.setIncumplimientoMedidaProteccion(true);
        e.setAccesoArmas(true);

        calculador.calcular(e);

        assertThat(e.isAlertaCritica()).isTrue();
    }

    @Test
    void debeListarFactoresRelevantesPresentes() {
        Evaluacion e = evaluacionBase();
        e.setAmenazaMuerte(true);
        e.setEstrangulamientoPrevio(true);

        calculador.calcular(e);

        assertThat(e.getFactoresRelevantes())
                .contains("Amenaza de muerte", "Estrangulamiento previo");
    }
}