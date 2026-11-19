package com.utp.proyectoriesgo.dto;
import java.time.LocalDateTime;
public record SeguimientoResponse(
        Long id,
        Long evaluacionId,
        String nota,
        LocalDateTime fecha
) {
}
