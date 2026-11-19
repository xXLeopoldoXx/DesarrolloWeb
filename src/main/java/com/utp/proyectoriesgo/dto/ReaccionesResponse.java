package com.utp.proyectoriesgo.dto;

import com.utp.proyectoriesgo.model.TipoReaccion;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Reacciones de una publicación: cuántas hay de cada tipo y cuáles dio la usuaria que consulta.
 * Nunca incluye QUIÉN reaccionó: solo totales (privacidad).
 */
public record ReaccionesResponse(Map<TipoReaccion, Long> conteos, List<TipoReaccion> mias) {

    /** Sin reacciones: todos los tipos en cero. */
    public static ReaccionesResponse vacia() {
        Map<TipoReaccion, Long> ceros = new EnumMap<>(TipoReaccion.class);
        for (TipoReaccion tipo : TipoReaccion.values()) {
            ceros.put(tipo, 0L);
        }
        return new ReaccionesResponse(ceros, List.of());
    }
}
