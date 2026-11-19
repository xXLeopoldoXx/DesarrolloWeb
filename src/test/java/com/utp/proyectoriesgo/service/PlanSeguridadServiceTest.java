package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.AccionRequest;
import com.utp.proyectoriesgo.dto.AccionResponse;
import com.utp.proyectoriesgo.dto.LugarRequest;
import com.utp.proyectoriesgo.dto.LugarResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.PlanAccion;
import com.utp.proyectoriesgo.repository.LugarSeguroRepository;
import com.utp.proyectoriesgo.repository.PlanAccionRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlanSeguridadServiceTest {
    private final PlanAccionRepository acciones = mock(PlanAccionRepository.class);
    private final LugarSeguroRepository lugares = mock(LugarSeguroRepository.class);
    private final PlanSeguridadService plan = new PlanSeguridadService(acciones, lugares);

    @Test
    void creaAccionPendienteYNormalizaTexto() {
        when(acciones.save(any())).thenAnswer(consulta -> consulta.getArgument(0));

        assertThat(plan.crearAccion(1L, new AccionRequest("  Preparar una mochila  ")))
                .isEqualTo(new AccionResponse(null, "Preparar una mochila", false));
    }

    @Test
    void permiteMarcarYDesmarcarUnaAccionPropia() {
        when(acciones.findByIdAndUsuarioId(10L, 1L))
                .thenReturn(Optional.of(new PlanAccion(1L, "Acción de prueba")));

        assertThat(plan.marcarAccion(1L, 10L, true).completada()).isTrue();
        assertThat(plan.marcarAccion(1L, 10L, false).completada()).isFalse();
    }

    @Test
    void rechazaMutacionesDeRegistrosAjenos() {
        assertThatThrownBy(() -> plan.marcarAccion(2L, 10L, true))
                .isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> plan.eliminarAccion(2L, 10L))
                .isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> plan.eliminarLugar(2L, 20L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void normalizaCamposOpcionalesYLimpiaLosDosRecursosPorUsuaria() {
        when(lugares.save(any())).thenAnswer(consulta -> consulta.getArgument(0));

        assertThat(plan.crearLugar(1L, new LugarRequest("  Lugar de prueba ", " ", null)))
                .isEqualTo(new LugarResponse(null, "Lugar de prueba", null, null));

        plan.eliminarDatosDe(1L);
        verify(acciones).deleteByUsuarioId(1L);
        verify(lugares).deleteByUsuarioId(1L);
    }
}
