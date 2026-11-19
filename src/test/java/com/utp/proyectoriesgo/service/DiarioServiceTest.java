package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.DiarioRequest;
import com.utp.proyectoriesgo.dto.DiarioResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.EntradaDiario;
import com.utp.proyectoriesgo.repository.EntradaDiarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiarioServiceTest {

    @Mock
    private EntradaDiarioRepository entradaDiarioRepository;

    private DiarioService diarioService;

    @BeforeEach
    void setUp() {
        diarioService = new DiarioService(entradaDiarioRepository);
    }

    @Test
    void listar_devuelveSoloEntradasDeLaUsuaria() {
        Long usuarioId = 10L;
        EntradaDiario e1 = new EntradaDiario(usuarioId, "Día tranquilo", LocalDateTime.now(), 4, "Calma", "Hoy me sentí bien.");
        e1.setId(1L);

        when(entradaDiarioRepository.findByUsuarioIdOrderByFechaDesc(usuarioId)).thenReturn(List.of(e1));

        List<DiarioResponse> resultado = diarioService.listar(usuarioId);

        assertEquals(1, resultado.size());
        assertEquals("Día tranquilo", resultado.get(0).titulo());
        assertEquals(usuarioId, resultado.get(0).usuarioId());
    }

    @Test
    void obtenerPorId_siEsPropia_devuelveEntrada() {
        Long usuarioId = 10L;
        Long entradaId = 1L;
        EntradaDiario e = new EntradaDiario(usuarioId, "Mi reflexión", LocalDateTime.now(), 3, "Esperanzada", "Texto privado.");
        e.setId(entradaId);

        when(entradaDiarioRepository.findByIdAndUsuarioId(entradaId, usuarioId)).thenReturn(Optional.of(e));

        DiarioResponse res = diarioService.obtenerPorId(entradaId, usuarioId);

        assertNotNull(res);
        assertEquals(entradaId, res.id());
        assertEquals("Mi reflexión", res.titulo());
    }

    @Test
    void obtenerPorId_siPerteneceAOtraUsuaria_lanzaRecursoNoEncontrado() {
        Long usuarioId = 10L;
        Long entradaAjenaId = 99L;

        when(entradaDiarioRepository.findByIdAndUsuarioId(entradaAjenaId, usuarioId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> diarioService.obtenerPorId(entradaAjenaId, usuarioId));
    }

    @Test
    void crear_guardaEntradaAsociadaAUsuaria() {
        Long usuarioId = 10L;
        DiarioRequest req = new DiarioRequest("Nuevo inicio", 5, "Felicidad", "Comenzando una nueva etapa.");

        when(entradaDiarioRepository.save(any(EntradaDiario.class))).thenAnswer(inv -> {
            EntradaDiario entidad = inv.getArgument(0);
            entidad.setId(50L);
            return entidad;
        });

        DiarioResponse res = diarioService.crear(usuarioId, req);

        assertNotNull(res);
        assertEquals(50L, res.id());
        assertEquals(usuarioId, res.usuarioId());
        assertEquals("Nuevo inicio", res.titulo());
        assertEquals(5, res.estadoAnimo());

        ArgumentCaptor<EntradaDiario> captor = ArgumentCaptor.forClass(EntradaDiario.class);
        verify(entradaDiarioRepository).save(captor.capture());
        assertEquals(usuarioId, captor.getValue().getUsuarioId());
    }

    @Test
    void actualizar_siEsPropia_modificaCampos() {
        Long usuarioId = 10L;
        Long entradaId = 2L;
        EntradaDiario existente = new EntradaDiario(usuarioId, "Título viejo", LocalDateTime.now(), 2, "Triste", "Texto viejo.");
        existente.setId(entradaId);

        when(entradaDiarioRepository.findByIdAndUsuarioId(entradaId, usuarioId)).thenReturn(Optional.of(existente));
        when(entradaDiarioRepository.save(any(EntradaDiario.class))).thenAnswer(inv -> inv.getArgument(0));

        DiarioRequest req = new DiarioRequest("Título actualizado", 4, "Aliviada", "Texto nuevo.");
        DiarioResponse res = diarioService.actualizar(entradaId, usuarioId, req);

        assertEquals("Título actualizado", res.titulo());
        assertEquals(4, res.estadoAnimo());
        assertEquals("Aliviada", res.emocion());
        assertEquals("Texto nuevo.", res.contenido());
    }

    @Test
    void actualizar_siEsDeOtraUsuaria_lanzaRecursoNoEncontrado() {
        Long usuarioId = 10L;
        Long entradaAjenaId = 99L;
        DiarioRequest req = new DiarioRequest("Intento", 3, "Neutro", "Texto");

        when(entradaDiarioRepository.findByIdAndUsuarioId(entradaAjenaId, usuarioId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> diarioService.actualizar(entradaAjenaId, usuarioId, req));
        verify(entradaDiarioRepository, never()).save(any());
    }

    @Test
    void eliminar_siEsPropia_eliminaEntidad() {
        Long usuarioId = 10L;
        Long entradaId = 3L;
        EntradaDiario existente = new EntradaDiario(usuarioId, "A borrar", LocalDateTime.now(), 1, "Mal", "...");
        existente.setId(entradaId);

        when(entradaDiarioRepository.findByIdAndUsuarioId(entradaId, usuarioId)).thenReturn(Optional.of(existente));

        diarioService.eliminar(entradaId, usuarioId);

        verify(entradaDiarioRepository).delete(existente);
    }

    @Test
    void eliminar_siEsDeOtraUsuaria_lanzaRecursoNoEncontrado() {
        Long usuarioId = 10L;
        Long entradaAjenaId = 99L;

        when(entradaDiarioRepository.findByIdAndUsuarioId(entradaAjenaId, usuarioId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> diarioService.eliminar(entradaAjenaId, usuarioId));
        verify(entradaDiarioRepository, never()).delete(any());
    }

    @Test
    void eliminarDatosDe_borraTodasLasEntradasDeLaUsuaria() {
        Long usuarioId = 10L;

        diarioService.eliminarDatosDe(usuarioId);

        verify(entradaDiarioRepository).deleteByUsuarioId(usuarioId);
    }
}
