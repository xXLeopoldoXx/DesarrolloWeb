package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LugarRequest(
        @NotBlank(message = "Escribe un nombre para el lugar.")
        @Size(max = 100, message = "El nombre debe tener hasta 100 caracteres.")
        String nombre,
        @Size(max = 250, message = "La referencia debe tener hasta 250 caracteres.")
        String referencia,
        @Size(max = 500, message = "La nota debe tener hasta 500 caracteres.")
        String nota) {
}
