package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Un mensaje anterior de la conversación (el cliente lo reenvía; el servidor no guarda nada).
 * El tope es mayor que el del mensaje nuevo (500) porque aquí también llegan las RESPUESTAS del asistente,
 * que pueden superar los 500 caracteres (GeminiClient permite hasta 500 tokens de salida).
 */
public record TurnoRequest(
        @NotBlank
        @Pattern(regexp = "usuaria|asistente", message = "El rol debe ser 'usuaria' o 'asistente'")
        String rol,

        @NotBlank
        @Size(max = 3000, message = "Cada mensaje del historial admite hasta 3000 caracteres")
        String texto) {
}
