package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;

public record SeguimientoRequest(
        @NotBlank(message = "La nota del seguimiento es obligatoria")
        String nota
) {
}