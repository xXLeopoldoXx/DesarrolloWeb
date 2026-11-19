package com.utp.proyectoriesgo.dto;

import com.utp.proyectoriesgo.model.Factor;
import com.utp.proyectoriesgo.model.Respuesta;
import jakarta.validation.constraints.NotNull;

public record RespuestaItem(
        @NotNull(message = "Falta indicar el factor.") Factor factor,
        @NotNull(message = "Falta indicar la respuesta.") Respuesta respuesta) {
}
