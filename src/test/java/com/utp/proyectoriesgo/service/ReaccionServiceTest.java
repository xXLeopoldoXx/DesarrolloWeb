package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.ReaccionesResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.ReaccionPublicacion;
import com.utp.proyectoriesgo.model.TipoReaccion;
import com.utp.proyectoriesgo.repository.PublicacionRepository;
import com.utp.proyectoriesgo.repository.ReaccionRepository;
import org.junit.jupiter.api.Test;

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

class ReaccionServiceTest {
    private final ReaccionRepository reacciones = mock(ReaccionRepository.class);
    private final PublicacionRepository publicaciones = mock(PublicacionRepository.class);
    private final ReaccionService servicio = new ReaccionService(reacciones, publicaciones);

    private Publicacion visible(Long id) {
        Publicacion publicacion = new Publicacion();
        when(publicaciones.findById(id)).thenReturn(Optional.of(publicacion));
        return publicacion;
    }

    private static List<Object[]> filas(Object[]... filas) {
        return new ArrayList<>(List.of(filas));
    }

    @Test
    void alternar_poneLaReaccionSiLaUsuariaNoLaHabiaDado() {
        visible(3L);
        when(reacciones.findByUsuarioIdAndPublicacionIdAndTipo(7L, 3L, TipoReaccion.ABRAZO))
                .thenReturn(Optional.empty());
        when(reacciones.contar(any())).thenReturn(filas(new Object[]{3L, TipoReaccion.ABRAZO, 1L}));
        when(reacciones.delasUsuaria(7L, List.of(3L))).thenReturn(filas(new Object[]{3L, TipoReaccion.ABRAZO}));

        ReaccionesResponse estado = servicio.alternar(7L, 3L, TipoReaccion.ABRAZO);

        verify(reacciones).save(any(ReaccionPublicacion.class));
        verify(reacciones, never()).delete(any());
        assertThat(estado.conteos()).containsEntry(TipoReaccion.ABRAZO, 1L).containsEntry(TipoReaccion.FUERZA, 0L);
        assertThat(estado.mias()).containsExactly(TipoReaccion.ABRAZO);
    }

    @Test
    void alternar_quitaLaReaccionSiLaUsuariaYaLaHabiaDado() {
        visible(3L);
        ReaccionPublicacion existente = new ReaccionPublicacion(7L, new Publicacion(), TipoReaccion.FUERZA);
        when(reacciones.findByUsuarioIdAndPublicacionIdAndTipo(7L, 3L, TipoReaccion.FUERZA))
                .thenReturn(Optional.of(existente));
        when(reacciones.contar(any())).thenReturn(filas());
        when(reacciones.delasUsuaria(7L, List.of(3L))).thenReturn(filas());

        ReaccionesResponse estado = servicio.alternar(7L, 3L, TipoReaccion.FUERZA);

        verify(reacciones).delete(existente);
        verify(reacciones, never()).save(any());
        assertThat(estado.mias()).isEmpty();
        assertThat(estado.conteos().values()).containsOnly(0L);
    }

    @Test
    void alternar_enUnaPublicacionInexistenteOOculta_responde404SinGuardarNada() {
        Publicacion oculta = new Publicacion();
        oculta.setOculta(true);
        when(publicaciones.findById(5L)).thenReturn(Optional.of(oculta));
        when(publicaciones.findById(6L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.alternar(7L, 5L, TipoReaccion.ABRAZO))
                .isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> servicio.alternar(7L, 6L, TipoReaccion.ABRAZO))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(reacciones, never()).save(any());
        verify(reacciones, never()).delete(any());
    }

    @Test
    void resumenes_combinaTotalesYPropiasYDejaEnCeroLasQueNoTienen() {
        when(reacciones.contar(any())).thenReturn(filas(
                new Object[]{1L, TipoReaccion.ABRAZO, 4L},
                new Object[]{1L, TipoReaccion.IDENTIFICO, 2L}));
        when(reacciones.delasUsuaria(7L, List.of(1L, 2L))).thenReturn(filas(
                new Object[]{1L, TipoReaccion.IDENTIFICO}));

        var resumen = servicio.resumenes(7L, List.of(1L, 2L));

        assertThat(resumen.get(1L).conteos())
                .containsEntry(TipoReaccion.ABRAZO, 4L)
                .containsEntry(TipoReaccion.IDENTIFICO, 2L)
                .containsEntry(TipoReaccion.FUERZA, 0L);
        assertThat(resumen.get(1L).mias()).containsExactly(TipoReaccion.IDENTIFICO);
        assertThat(resumen.get(2L).conteos().values()).containsOnly(0L);
        assertThat(resumen.get(2L).mias()).isEmpty();
    }

    @Test
    void resumenes_sinPublicaciones_noConsultaLaBase() {
        assertThat(servicio.resumenes(7L, List.of())).isEmpty();

        verify(reacciones, never()).contar(any());
        verify(reacciones, never()).delasUsuaria(any(), any());
    }
}
