package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotNull;

public record AccionEstadoRequest(
        @NotNull(message = "Indica si la acción está completada.")
        Boolean completada) {
}
