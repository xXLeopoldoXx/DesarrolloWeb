package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Un mensaje anterior de la conversación (el cliente lo reenvía; el servidor no guarda nada). */
public record TurnoRequest(
        @NotBlank
        @Pattern(regexp = "usuaria|asistente", message = "El rol debe ser 'usuaria' o 'asistente'")
        String rol,

        @NotBlank
        @Size(max = 500, message = "Cada mensaje admite hasta 500 caracteres")
        String texto) {
}
