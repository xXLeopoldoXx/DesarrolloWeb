package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.AutoevaluacionRequest;
import com.utp.proyectoriesgo.dto.RespuestaItem;
import com.utp.proyectoriesgo.dto.ResultadoResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.exception.ReglaNegocioException;
import com.utp.proyectoriesgo.model.EvaluacionUsuaria;
import com.utp.proyectoriesgo.model.Factor;
import com.utp.proyectoriesgo.model.RespuestaEvaluacion;
import com.utp.proyectoriesgo.model.Respuesta;
import com.utp.proyectoriesgo.repository.EvaluacionUsuariaRepository;
import com.utp.proyectoriesgo.repository.RespuestaEvaluacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutoevaluacionServiceTest {
    private final EvaluacionUsuariaRepository evaluaciones = mock(EvaluacionUsuariaRepository.class);
    private final RespuestaEvaluacionRepository respuestas = mock(RespuestaEvaluacionRepository.class);
    private final AutoevaluacionService servicio =
            new AutoevaluacionService(evaluaciones, respuestas, new CalculadoraAutoevaluacion());

    private static List<RespuestaItem> todasEn(Respuesta respuesta) {
        List<RespuestaItem> items = new ArrayList<>();
        for (Factor factor : Factor.values()) {
            items.add(new RespuestaItem(factor, respuesta));
        }
        return items;
    }

    private static EvaluacionUsuaria evaluacionGuardada(Long id, Long usuarioId, int puntaje, String nivel,
                                                        boolean alerta) {
        EvaluacionUsuaria e = new EvaluacionUsuaria(usuarioId, LocalDateTime.of(2026, 11, 19, 10, 0),
                puntaje, nivel, alerta);
        ReflectionTestUtils.setField(e, "id", id);
        return e;
    }

    @Test
    void creaLaEvaluacionDeLaUsuariaConSuResultadoYSusNueveRespuestas() {
        when(evaluaciones.save(any())).thenAnswer(llamada -> {
            EvaluacionUsuaria e = llamada.getArgument(0);
            ReflectionTestUtils.setField(e, "id", 7L);
            return e;
        });
        List<RespuestaItem> items = todasEn(Respuesta.NO);
        items.set(0, new RespuestaItem(Factor.AMENAZA_MUERTE, Respuesta.SI));

        ResultadoResponse resultado = servicio.crear(1L, new AutoevaluacionRequest(items));

        assertThat(resultado.id()).isEqualTo(7L);
        assertThat(resultado.fecha()).isNotNull();
        assertThat(resultado.puntaje()).isEqualTo(25);
        assertThat(resultado.nivel()).isEqualTo("BAJO");
        assertThat(resultado.senales()).containsExactly("Amenaza de muerte");
        verify(respuestas).saveAll(org.mockito.ArgumentMatchers.<Iterable<RespuestaEvaluacion>>argThat(
                guardadas -> guardadas.spliterator().getExactSizeIfKnown() == 9));
    }

    @Test
    void rechazaUnaEvaluacionIncompletaORepetidaSinGuardarNada() {
        List<RespuestaItem> incompleta = todasEn(Respuesta.NO).subList(0, 8);
        List<RespuestaItem> repetida = todasEn(Respuesta.NO);
        repetida.set(1, new RespuestaItem(Factor.AMENAZA_MUERTE, Respuesta.SI));

        assertThatThrownBy(() -> servicio.crear(1L, new AutoevaluacionRequest(incompleta)))
                .isInstanceOf(ReglaNegocioException.class);
        assertThatThrownBy(() -> servicio.crear(1L, new AutoevaluacionRequest(repetida)))
                .isInstanceOf(ReglaNegocioException.class);
        verify(evaluaciones, never()).save(any());
        verify(respuestas, never()).saveAll(any());
    }

    @Test
    void listaSoloLasEvaluacionesDeLaUsuaria() {
        when(evaluaciones.findByUsuarioIdOrderByFechaDesc(1L))
                .thenReturn(List.of(evaluacionGuardada(5L, 1L, 40, "MEDIO", false)));

        var lista = servicio.listar(1L);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).id()).isEqualTo(5L);
        assertThat(lista.get(0).nivel()).isEqualTo("MEDIO");
        assertThat(servicio.listar(2L)).isEmpty();
    }

    @Test
    void elDetalleUsaElResultadoGuardadoYReconstruyeLosTextos() {
        when(evaluaciones.findByIdAndUsuarioId(5L, 1L))
                .thenReturn(Optional.of(evaluacionGuardada(5L, 1L, 25, "BAJO", false)));
        when(respuestas.findByEvaluacionId(5L)).thenReturn(List.of(
                new RespuestaEvaluacion(5L, Factor.AMENAZA_MUERTE, Respuesta.SI),
                new RespuestaEvaluacion(5L, Factor.ACCESO_ARMAS, Respuesta.NO_SEGURA)));

        ResultadoResponse detalle = servicio.detalle(1L, 5L);

        assertThat(detalle.puntaje()).isEqualTo(25);
        assertThat(detalle.nivel()).isEqualTo("BAJO");
        assertThat(detalle.senales()).containsExactly("Amenaza de muerte");
        assertThat(detalle.porConfirmar()).containsExactly("Acceso a armas");
    }

    @Test
    void unaEvaluacionAjenaResponde404EnDetalleYEliminar() {
        // La repo no la encuentra para la usuaria 2 (es de la 1): se comporta como inexistente.
        assertThatThrownBy(() -> servicio.detalle(2L, 5L)).isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> servicio.eliminar(2L, 5L)).isInstanceOf(RecursoNoEncontradoException.class);
        verify(respuestas, never()).deleteByEvaluacionIdIn(any());
        verify(evaluaciones, never()).delete(any());
    }

    @Test
    void eliminarBorraPrimeroLasRespuestasYLuegoLaEvaluacion() {
        EvaluacionUsuaria propia = evaluacionGuardada(5L, 1L, 0, "BAJO", false);
        when(evaluaciones.findByIdAndUsuarioId(5L, 1L)).thenReturn(Optional.of(propia));

        servicio.eliminar(1L, 5L);

        var orden = org.mockito.Mockito.inOrder(respuestas, evaluaciones);
        orden.verify(respuestas).deleteByEvaluacionIdIn(List.of(5L));
        orden.verify(evaluaciones).delete(propia);
    }

    @Test
    void eliminarDatosDeBorraTodoLoDeLaUsuariaYEsIdempotente() {
        when(evaluaciones.findByUsuarioIdOrderByFechaDesc(1L)).thenReturn(List.of(
                evaluacionGuardada(5L, 1L, 0, "BAJO", false), evaluacionGuardada(6L, 1L, 0, "BAJO", false)));

        servicio.eliminarDatosDe(1L);
        servicio.eliminarDatosDe(9L); // sin datos: no falla ni toca respuestas

        verify(respuestas).deleteByEvaluacionIdIn(List.of(5L, 6L));
        verify(evaluaciones).deleteByUsuarioId(1L);
        verify(evaluaciones).deleteByUsuarioId(9L);
        verify(respuestas, org.mockito.Mockito.times(1)).deleteByEvaluacionIdIn(any());
    }
}
