package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.RespuestaItem;
import com.utp.proyectoriesgo.dto.ResultadoResponse;
import com.utp.proyectoriesgo.model.Factor;
import com.utp.proyectoriesgo.model.Respuesta;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Calcula el resultado de la autoevaluación con las mismas reglas que {@link RiesgoCalculador}:
 * cada «Sí» suma el peso de su factor, «No estoy segura» no suma y queda por confirmar, y la alerta
 * crítica sube el nivel a ALTO como mínimo. El resultado es orientativo, nunca una probabilidad.
 */
@Component
public class CalculadoraAutoevaluacion {

    public ResultadoResponse calcular(List<RespuestaItem> respuestas) {
        Set<Factor> confirmados = EnumSet.noneOf(Factor.class);
        List<String> senales = new ArrayList<>();
        List<String> porConfirmar = new ArrayList<>();
        int puntaje = 0;

        for (RespuestaItem item : respuestas) {
            if (item.respuesta() == Respuesta.SI) {
                confirmados.add(item.factor());
                puntaje += item.factor().getPeso();
                senales.add(item.factor().getTexto());
            } else if (item.respuesta() == Respuesta.NO_SEGURA) {
                porConfirmar.add(item.factor().getTexto());
            }
        }

        boolean alertaCritica = alertaCritica(confirmados);
        return new ResultadoResponse(null, null, nivel(puntaje, alertaCritica), puntaje, alertaCritica,
                List.copyOf(senales), List.copyOf(porConfirmar));
    }

    private static boolean alertaCritica(Set<Factor> confirmados) {
        boolean amenazaYEstrangulamiento = confirmados.contains(Factor.AMENAZA_MUERTE)
                && confirmados.contains(Factor.ESTRANGULAMIENTO_PREVIO);
        boolean incumplimientoYArmas = confirmados.contains(Factor.INCUMPLIMIENTO_MEDIDA_PROTECCION)
                && confirmados.contains(Factor.ACCESO_ARMAS);
        return amenazaYEstrangulamiento || incumplimientoYArmas;
    }

    private static String nivel(int puntaje, boolean alertaCritica) {
        String nivel;
        if (puntaje <= 30) {
            nivel = "BAJO";
        } else if (puntaje <= 60) {
            nivel = "MEDIO";
        } else if (puntaje <= 80) {
            nivel = "ALTO";
        } else {
            nivel = "MUY_ALTO";
        }
        if (alertaCritica && (nivel.equals("BAJO") || nivel.equals("MEDIO"))) {
            return "ALTO";
        }
        return nivel;
    }
}
