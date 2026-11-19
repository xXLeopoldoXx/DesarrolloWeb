
package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicacionRequest(

    @NotBlank(message = "El texto no puede estar vacío")
    @Size(max = 500, message = "Máximo 500 caracteres")
    String texto

) {}
