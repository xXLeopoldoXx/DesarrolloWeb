package com.utp.proyectoriesgo.dto;

import java.time.LocalDateTime;

public record ResumenEvaluacionResponse(Long id, LocalDateTime fecha, String nivel) {
}
