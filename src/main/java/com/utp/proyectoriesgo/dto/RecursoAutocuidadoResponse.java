package com.utp.proyectoriesgo.dto;

public record RecursoAutocuidadoResponse(
        Long id,
        String categoria,
        String titulo,
        String descripcion,
        String tipo,
        String enlaceOConsejo
) {
}
