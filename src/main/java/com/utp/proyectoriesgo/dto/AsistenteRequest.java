package com.utp.proyectoriesgo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AsistenteRequest(
        @NotBlank(message = "Escribe un mensaje")
        @Size(max = 500, message = "El mensaje admite hasta 500 caracteres")
        String mensaje,

        // Últimos mensajes de la conversación, para dar contexto. Opcional.
        @Size(max = 6, message = "El historial admite hasta 6 mensajes")
        List<@Valid TurnoRequest> historial) {
}
