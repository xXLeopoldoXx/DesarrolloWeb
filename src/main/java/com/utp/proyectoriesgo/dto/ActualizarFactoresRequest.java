package com.utp.proyectoriesgo.dto;

// DTO para PATCH: solo los factores de riesgo, que son los que cambian
// dinámicamente en el tiempo (ver "riesgo dinámico" en el documento conceptual).
// No incluye seudónimo, edad ni relación, porque esos no se actualizan aquí.
public record ActualizarFactoresRequest(
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