package com.utp.proyectoriesgo.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ReporteResponse(
        LocalDateTime fechaServidor,
        long totalEvaluaciones,
        Map<String, Long> porNivel) {
}
