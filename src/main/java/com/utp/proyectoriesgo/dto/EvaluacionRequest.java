package com.utp.proyectoriesgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EvaluacionRequest(
        @NotBlank(message = "El seudónimo de la víctima es obligatorio")
        String seudonimoVictima,
        
        @NotNull(message = "La edad es obligatoria")
        @Positive(message = "La edad debe ser un número positivo")
        Integer edadVictima,
        
        @NotBlank(message = "La relación con el agresor no puede estar vacía")
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
) {}