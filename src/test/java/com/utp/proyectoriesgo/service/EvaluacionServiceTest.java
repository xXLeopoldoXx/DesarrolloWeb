package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.EvaluacionRequest;
import com.utp.proyectoriesgo.exception.EvaluacionNoEncontradaException;
import com.utp.proyectoriesgo.exception.ReglaNegocioException;
import com.utp.proyectoriesgo.model.Evaluacion;
import com.utp.proyectoriesgo.model.Seguimiento;
import com.utp.proyectoriesgo.repository.EvaluacionRepository;
import com.utp.proyectoriesgo.repository.SeguimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.utp.proyectoriesgo.dto.ReporteResponse;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionServiceTest {

    @Mock
    private EvaluacionRepository repository;

    @Mock
    private SeguimientoRepository seguimientoRepository;

    private EvaluacionService service;

    @BeforeEach
    void setUp() {
        service = new EvaluacionService(repository, seguimientoRepository, new RiesgoCalculador());
    }

    private EvaluacionRequest requestValido() {
        return new EvaluacionRequest(
                "Caso-Test", 28, "Pareja",
                true, true, true, true, true, true, true, true, true, true, true
        );
    }

    private Evaluacion evaluacionExistente() {
        return new Evaluacion(1L, "Caso-Test", 28, "Pareja",
                false, false, false, false, false, false, false, false, false, false, false);
    }

    // ----- Lab 6 -----

    @Test
    void crear_debeGuardarYCalcularRiesgo() {
        when(repository.save(any(Evaluacion.class))).thenAnswer(inv -> {
            Evaluacion e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        Evaluacion creada = service.crear(requestValido());

        assertThat(creada.getId()).isEqualTo(1L);
        assertThat(creada.getNivelRiesgo()).isNotNull();
    }

    @Test
    void crear_conSeudonimoVacio_debeLanzarExcepcion() {
        EvaluacionRequest invalido = new EvaluacionRequest(
                "", 28, "Pareja",
                false, false, false, false, false, false, false, false, false, false, false
        );

        assertThatThrownBy(() -> service.crear(invalido))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }

    @Test
    void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.eliminar(999L))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }

    // ----- Lab 7 -----

    @Test
    void buscarPorNivelRiesgo_conNivelInvalido_debeLanzarReglaNegocio() {
        assertThatThrownBy(() -> service.buscarPorNivelRiesgo("EXTREMO"))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void buscarPorRangoFecha_conRangoInvertido_debeLanzarReglaNegocio() {
        LocalDateTime desde = LocalDateTime.now();
        LocalDateTime hasta = desde.minusDays(1);

        assertThatThrownBy(() -> service.buscarPorRangoFecha(desde, hasta))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void registrarSeguimiento_conNotaVacia_debeLanzarReglaNegocio() {
        assertThatThrownBy(() -> service.registrarSeguimiento(1L, "   "))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void registrarSeguimiento_valido_debeGuardarYActualizarFechaDelCaso() {
        Evaluacion evaluacion = evaluacionExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(seguimientoRepository.save(any(Seguimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        Seguimiento s = service.registrarSeguimiento(1L, "Nueva amenaza por mensaje");

        assertThat(s.getNota()).isEqualTo("Nueva amenaza por mensaje");
        assertThat(evaluacion.getFechaUltimoSeguimiento()).isNotNull();
    }

    @Test
    void simularErrorSeguimiento_debeLanzarIllegalStateException() {
        Evaluacion evaluacion = evaluacionExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(seguimientoRepository.save(any(Seguimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThatThrownBy(() -> service.simularErrorSeguimiento(1L, "Nota de prueba"))
                .isInstanceOf(IllegalStateException.class);
    }

        @Test
    void generarReporte_cuentaTotalYPorNivel() {
        when(repository.count()).thenReturn(5L);
        when(repository.buscarPorNivelRiesgo("BAJO")).thenReturn(List.of(new Evaluacion()));

        ReporteResponse reporte = service.generarReporte();

        assertThat(reporte.totalEvaluaciones()).isEqualTo(5L);
        assertThat(reporte.porNivel().get("BAJO")).isEqualTo(1L);
        assertThat(reporte.porNivel().get("MUY_ALTO")).isEqualTo(0L);
        assertThat(reporte.fechaServidor()).isNotNull();
    }
}