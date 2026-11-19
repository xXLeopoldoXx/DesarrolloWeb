package com.utp.semana02.dto;
import java.time.LocalDateTime;
public record ErrorResponse(
 LocalDateTime fecha,
 int estado,
 String error,
 String mensaje,
 String ruta
) {
}
