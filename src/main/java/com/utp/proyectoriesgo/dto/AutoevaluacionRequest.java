package com.utp.proyectoriesgo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AutoevaluacionRequest(
        @NotEmpty(message = "Responde las preguntas del cuestionario.")
        List<@NotNull(message = "Hay una respuesta vacía.") @Valid RespuestaItem> respuestas) {
}
