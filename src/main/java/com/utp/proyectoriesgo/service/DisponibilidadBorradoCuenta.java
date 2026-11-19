package com.utp.proyectoriesgo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class DisponibilidadBorradoCuenta {
    private static final Set<String> LIMPIEZAS_REQUERIDAS = Set.of(
            "planSeguridadService", "contactoService", "autoevaluacionService", "diarioService", "comunidadService");

    private final boolean disponible;

    public DisponibilidadBorradoCuenta(Map<String, LimpiezaDatosUsuaria> limpiezas,
                                       @Value("${app.seguridad.borrado-cuenta.habilitado:false}") boolean autorizado) {
        disponible = autorizado && limpiezas.keySet().containsAll(LIMPIEZAS_REQUERIDAS);
    }

    public boolean disponible() {
        return disponible;
    }
}
