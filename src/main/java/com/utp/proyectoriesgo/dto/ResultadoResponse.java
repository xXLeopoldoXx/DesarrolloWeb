package com.utp.proyectoriesgo.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resultado de una autoevaluación. Es orientativo: NO es una predicción ni una probabilidad de feminicidio.
 *
 * @param senales      textos de los factores contestados «Sí»
 * @param porConfirmar textos de los factores contestados «No estoy segura»
 */
public record ResultadoResponse(
        Long id,
        LocalDateTime fecha,
        String nivel,
        int puntaje,
        boolean alertaCritica,
        List<String> senales,
        List<String> porConfirmar) {

    /** Copia el resultado calculado añadiéndole el id y la fecha con que se guardó. */
    public ResultadoResponse conIdentidad(Long nuevoId, LocalDateTime nuevaFecha) {
        return new ResultadoResponse(nuevoId, nuevaFecha, nivel, puntaje, alertaCritica, senales, porConfirmar);
    }
}
