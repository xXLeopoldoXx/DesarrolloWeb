package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.BorradoCuentaNoDisponibleException;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
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
    private final InvalidacionSesionesCuenta sesiones = mock(InvalidacionSesionesCuenta.class);
    private final Map<String, LimpiezaDatosUsuaria> limpiezas = new LinkedHashMap<>(Map.of(
            "planSeguridadService", mock(LimpiezaDatosUsuaria.class),
            "contactoService", mock(LimpiezaDatosUsuaria.class),
            "autoevaluacionService", mock(LimpiezaDatosUsuaria.class),
            "diarioService", mock(LimpiezaDatosUsuaria.class),
            "comunidadService", mock(LimpiezaDatosUsuaria.class)));

    @Test
    void bloqueaElBorradoSiFaltaAprobacionUnaLimpiezaOInvalidacion() {
        var sinAprobacion = new CuentaService(usuarias, limpiezas, Optional.of(sesiones), false);
        var sinInvalidacion = new CuentaService(usuarias, limpiezas, Optional.empty(), true);
        limpiezas.remove("comunidadService");
        var sinLimpieza = new CuentaService(usuarias, limpiezas, Optional.of(sesiones), true);

        for (CuentaService servicio : new CuentaService[]{sinAprobacion, sinInvalidacion, sinLimpieza}) {
            assertThat(servicio.borradoDisponible()).isFalse();
            assertThatThrownBy(() -> servicio.eliminarCuenta(usuaria))
                    .isInstanceOf(BorradoCuentaNoDisponibleException.class);
        }
        verifyNoInteractions(usuaria, usuarias, sesiones);
        limpiezas.values().forEach(limpieza -> verifyNoInteractions(limpieza));
    }

    @Test
    void ejecutaTodasLasLimpiezasEInvalidacionAntesDeBorrarLaCuenta() {
        LimpiezaDatosUsuaria adicional = mock(LimpiezaDatosUsuaria.class);
        limpiezas.put("limpiezaAdicional", adicional);
        var servicio = new CuentaService(usuarias, limpiezas, Optional.of(sesiones), true);
        when(usuaria.getId()).thenReturn(1L);
        when(usuarias.findById(1L)).thenReturn(Optional.of(usuaria));

        assertThat(servicio.borradoDisponible()).isTrue();
        servicio.eliminarCuenta(usuaria);

        limpiezas.values().forEach(limpieza -> verify(limpieza).eliminarDatosDe(1L));
        var orden = inOrder(adicional, sesiones, usuarias);
        orden.verify(adicional).eliminarDatosDe(1L);
        orden.verify(sesiones).invalidarSesionesDe(1L);
        orden.verify(usuarias).delete(usuaria);
    }

    @Test
    void unFalloDeInvalidacionInterrumpeLaEliminacionDeLaCuenta() {
        var servicio = new CuentaService(usuarias, limpiezas, Optional.of(sesiones), true);
        when(usuaria.getId()).thenReturn(1L);
        when(usuarias.findById(1L)).thenReturn(Optional.of(usuaria));
        doThrow(new IllegalStateException("Fallo de prueba")).when(sesiones).invalidarSesionesDe(1L);

        assertThatThrownBy(() -> servicio.eliminarCuenta(usuaria)).isInstanceOf(IllegalStateException.class);
        verify(usuarias, never()).delete(usuaria);
    }
}
