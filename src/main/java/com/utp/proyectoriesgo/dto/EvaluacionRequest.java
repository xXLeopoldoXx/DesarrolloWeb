package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EvaluacionRequest(
        @NotBlank(message = "El seudónimo de la víctima es obligatorio")
        String seudonimoVictima,

        @NotNull(message = "La edad de la víctima es obligatoria")
        @Positive(message = "La edad debe ser mayor a cero")
        Integer edadVictima,

        @NotBlank(message = "La relación con el agresor es obligatoria")
        String relacionAgresor,

        boolean amenazaMuerte,
        boolean violenciaFisicaPrevia,
        boolean estrangulamientoPrevio,
        boolean accesoArmas,
        boolean incumplimientoMedidaProteccion,
        boolean acosoOPersecucion,
        boolean separacionReciente,
        boolean controlExtremo,
        boolean consumoAlcoholDrogasAgresor,
        boolean denunciasPrevias,
        boolean hijosEnComun
) {
}