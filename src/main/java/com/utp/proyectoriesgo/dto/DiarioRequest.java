package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DiarioRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no puede superar 150 caracteres")
        String titulo,

        @Min(value = 1, message = "El estado de ánimo debe estar entre 1 y 5")
        @Max(value = 5, message = "El estado de ánimo debe estar entre 1 y 5")
        int estadoAnimo,

        @Size(max = 50, message = "La emoción no puede superar 50 caracteres")
        String emocion,

        @NotBlank(message = "El contenido es obligatorio")
        @Size(max = 2000, message = "El contenido no puede superar 2000 caracteres")
        String contenido
) {
}
