package com.utp.proyectoriesgo.dto;

import com.utp.proyectoriesgo.model.TipoReaccion;
import jakarta.validation.constraints.NotNull;

public record ReaccionRequest(@NotNull(message = "Indica el tipo de reacción.") TipoReaccion tipo) {
}
