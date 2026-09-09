package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.EvaluacionRequest;
import com.utp.proyectoriesgo.exception.EvaluacionNoEncontradaException;
import com.utp.proyectoriesgo.model.Evaluacion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionServiceTest {

    private final EvaluacionService service = new EvaluacionService(new RiesgoCalculador());

    private EvaluacionRequest requestValido() {
        return new EvaluacionRequest(
                "Caso-Test", 28, "Pareja",
                true, true, true, true, true, true, true, true, true, true, true
        );
    }

    @Test
    void crear_debeAsignarIdYCalcularRiesgo() {
        Evaluacion creada = service.crear(requestValido());

        assertThat(creada.getId()).isNotNull();
        assertThat(creada.getPuntajeRiesgo()).isGreaterThan(0);
        assertThat(creada.getNivelRiesgo()).isNotEqualTo("PENDIENTE");
        assertThat(service.listar()).hasSize(1);
    }

    @Test
    void crear_conSeudonimoVacio_debeLanzarExcepcion() {
        EvaluacionRequest invalido = new EvaluacionRequest(
                "", 28, "Pareja",
                false, false, false, false, false, false, false, false, false, false, false
        );

        assertThatThrownBy(() -> service.crear(invalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El seudónimo de la víctima es obligatorio.");
    }
}
