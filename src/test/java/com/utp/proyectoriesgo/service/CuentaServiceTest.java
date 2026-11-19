package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.BorradoCuentaNoDisponibleException;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CuentaServiceTest {
    private final UsuarioRepository usuarias = mock(UsuarioRepository.class);
    private final Usuario usuaria = mock(Usuario.class);
    private final LimpiezaDatosUsuaria primera = mock(LimpiezaDatosUsuaria.class);
    private final LimpiezaDatosUsuaria segunda = mock(LimpiezaDatosUsuaria.class);

    @Test
    void bloqueaElBorradoSiFaltaAutorizacionOUnModulo() {
        var soloMiSeguridad = Map.of("planSeguridadService", primera, "contactoService", segunda);
        var sinModulos = new DisponibilidadBorradoCuenta(soloMiSeguridad, true);
        assertThat(sinModulos.disponible()).isFalse();
        assertThat(new DisponibilidadBorradoCuenta(nombresRequeridos(), false).disponible()).isFalse();
        assertThat(new DisponibilidadBorradoCuenta(nombresRequeridos(), true).disponible()).isTrue();

        var servicio = new CuentaService(usuarias, List.of(primera, segunda), sinModulos);
        assertThatThrownBy(() -> servicio.eliminarCuenta(usuaria))
                .isInstanceOf(BorradoCuentaNoDisponibleException.class);
        verifyNoInteractions(usuarias, primera, segunda);
    }

    @Test
    void bloqueaLaFilaYEjecutaTodasLasLimpiezasAntesDeEliminar() {
        var servicio = servicioAutorizado();
        when(usuaria.getId()).thenReturn(1L);
        when(usuarias.buscarParaBorrado(1L)).thenReturn(Optional.of(usuaria));

        servicio.eliminarCuenta(usuaria);

        var orden = inOrder(usuarias, primera, segunda);
        orden.verify(usuarias).buscarParaBorrado(1L);
        orden.verify(primera).eliminarDatosDe(1L);
        orden.verify(segunda).eliminarDatosDe(1L);
        orden.verify(usuarias).delete(usuaria);
    }

    @Test
    void unFalloDeLimpiezaInterrumpeLaEliminacionDeLaCuenta() {
        var servicio = servicioAutorizado();
        when(usuaria.getId()).thenReturn(1L);
        when(usuarias.buscarParaBorrado(1L)).thenReturn(Optional.of(usuaria));
        doThrow(new IllegalStateException("Fallo de prueba")).when(segunda).eliminarDatosDe(1L);

        assertThatThrownBy(() -> servicio.eliminarCuenta(usuaria)).isInstanceOf(IllegalStateException.class);
        verify(usuarias, never()).delete(usuaria);
    }

    private CuentaService servicioAutorizado() {
        var disponibilidad = mock(DisponibilidadBorradoCuenta.class);
        when(disponibilidad.disponible()).thenReturn(true);
        return new CuentaService(usuarias, List.of(primera, segunda), disponibilidad);
    }

    private Map<String, LimpiezaDatosUsuaria> nombresRequeridos() {
        // Verifica las claves de la condición. Ninguna de estas referencias se registra ni se ejecuta.
        return Map.of("planSeguridadService", primera, "contactoService", segunda,
                "autoevaluacionService", primera, "diarioService", primera, "comunidadService", primera);
    }
}
