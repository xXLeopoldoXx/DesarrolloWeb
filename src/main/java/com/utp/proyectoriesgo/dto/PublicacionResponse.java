
package com.utp.proyectoriesgo.dto;

import java.time.LocalDateTime;

public record PublicacionResponse(
    Long id,
    String seudonimo,
    String texto,
    LocalDateTime fecha
) {}
