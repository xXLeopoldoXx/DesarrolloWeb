package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccionRequest(
        @NotBlank(message = "Escribe una acción.")
        @Size(max = 240, message = "La acción debe tener hasta 240 caracteres.")
        String texto) {
}
