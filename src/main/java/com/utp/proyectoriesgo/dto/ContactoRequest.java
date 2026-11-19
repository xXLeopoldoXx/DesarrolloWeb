package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactoRequest(
        @NotBlank(message = "Escribe un nombre para el contacto.")
        @Size(max = 100, message = "El nombre debe tener hasta 100 caracteres.")
        String nombre,
        @NotBlank(message = "Escribe un teléfono.")
        @Pattern(regexp = "\\+?[0-9]{6,15}", message = "Usa de 6 a 15 dígitos y un signo + opcional al inicio.")
        String telefono,
        @Size(max = 100, message = "La relación debe tener hasta 100 caracteres.")
        String relacion) {
}
