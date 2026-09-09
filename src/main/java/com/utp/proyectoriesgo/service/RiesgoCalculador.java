package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.model.Evaluacion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RiesgoCalculador {

    public void calcular(Evaluacion evaluacion) {
        int puntaje = 0;
        List<String> factores = new ArrayList<>();

        if (evaluacion.isAmenazaMuerte()) {
            puntaje += 25;
            factores.add("Amenaza de muerte");
        }
        if (evaluacion.isAccesoArmas()) {
            puntaje += 20;
            factores.add("Acceso a armas");
        }
        if (evaluacion.isEstrangulamientoPrevio()) {
            puntaje += 30;
            factores.add("Estrangulamiento previo");
        }
        if (evaluacion.isDenunciasPrevias()) {
            puntaje += 10;
            factores.add("Denuncias previas");
        }
        if (evaluacion.isIncumplimientoMedidaProteccion()) {
            puntaje += 15;
            factores.add("Incumplimiento de medida de protección");
        }
        if (evaluacion.isAcosoOPersecucion()) {
            puntaje += 15;
            factores.add("Acoso o persecución");
        }
        if (evaluacion.isSeparacionReciente()) {
            puntaje += 10;
            factores.add("Separación reciente");
        }
        if (evaluacion.isControlExtremo()) {
            puntaje += 10;
            factores.add("Control extremo");
        }
        if (evaluacion.isConsumoAlcoholDrogasAgresor()) {
            puntaje += 5;
            factores.add("Consumo problemático del agresor");
        }

        boolean alertaCritica = detectarAlertaCritica(evaluacion);

        evaluacion.setPuntajeRiesgo(puntaje);
        evaluacion.setFactoresRelevantes(factores);
        evaluacion.setAlertaCritica(alertaCritica);
        evaluacion.setNivelRiesgo(determinarNivel(puntaje, alertaCritica));
    }

    private boolean detectarAlertaCritica(Evaluacion e) {
        boolean amenazaMasEstrangulamiento = e.isAmenazaMuerte() && e.isEstrangulamientoPrevio();
        boolean incumplimientoMasArmas = e.isIncumplimientoMedidaProteccion() && e.isAccesoArmas();
        return amenazaMasEstrangulamiento || incumplimientoMasArmas;
    }

    private String determinarNivel(int puntaje, boolean alertaCritica) {
        String nivelPorPuntaje;
        if (puntaje <= 30) {
            nivelPorPuntaje = "BAJO";
        } else if (puntaje <= 60) {
            nivelPorPuntaje = "MEDIO";
        } else if (puntaje <= 80) {
            nivelPorPuntaje = "ALTO";
        } else {
            nivelPorPuntaje = "MUY_ALTO";
        }

        if (alertaCritica && (nivelPorPuntaje.equals("BAJO") || nivelPorPuntaje.equals("MEDIO"))) {
            return "ALTO";
        }
        return nivelPorPuntaje;
    }
}