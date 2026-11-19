package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Pattern(regexp = "^[a-z0-9._-]{4,40}$",
                message = "Use de 4 a 40 caracteres: minúsculas, números, punto, guion o guion bajo")
        String username,

        // BCrypt solo usa los primeros 72 bytes: por eso el máximo.
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
                message = "Incluya al menos una mayúscula y un número")
        String password,

        // Nombre con el que la usuaria se muestra (por ejemplo en la comunidad).
        // No es su nombre real: se recomienda un seudónimo.
        @NotBlank(message = "El seudónimo es obligatorio")
        @Size(min = 2, max = 40, message = "El seudónimo debe tener entre 2 y 40 caracteres")
        String seudonimo) {
}
