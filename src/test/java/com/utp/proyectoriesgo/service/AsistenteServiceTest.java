package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.AsistenteRequest;
import com.utp.proyectoriesgo.dto.AsistenteResponse;
import com.utp.proyectoriesgo.dto.TurnoRequest;
import com.utp.proyectoriesgo.exception.LimiteMensajesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsistenteServiceTest {

    @Mock
    private GeminiClient gemini;

    private AsistenteService servicio;

    @BeforeEach
    void preparar() {
        // Límite real de 2 mensajes para poder probarlo
        servicio = new AsistenteService(gemini, new DetectorPeligro(),
                new LimitadorMensajes(2, Duration.ofHours(1), Clock.systemUTC()));
    }

    private static AsistenteRequest pregunta(String mensaje) {
        return new AsistenteRequest(mensaje, null);
    }

    @Test
    void preguntaNormal_llamaAlModeloConLasInstruccionesYDevuelveSuRespuesta() {
        when(gemini.generar(eq(AsistenteService.INSTRUCCIONES), anyList())).thenReturn("Un plan de seguridad es...");

        AsistenteResponse respuesta = servicio.responder("maria.l", pregunta("  ¿Qué es un plan de seguridad? "));

        assertEquals("Un plan de seguridad es...", respuesta.respuesta());
        assertFalse(respuesta.emergencia());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GeminiClient.Turno>> captor = ArgumentCaptor.forClass(List.class);
        verify(gemini).generar(eq(AsistenteService.INSTRUCCIONES), captor.capture());
        assertEquals(List.of(new GeminiClient.Turno("user", "¿Qué es un plan de seguridad?")), captor.getValue());
    }

    @Test
    void mensajeDePeligro_respondeTextoFijoSinLlamarAlModelo() {
        AsistenteResponse respuesta = servicio.responder("maria.l", pregunta("Mi pareja me va a matar"));

        assertTrue(respuesta.emergencia());
        assertTrue(respuesta.respuesta().contains("105"));
        assertTrue(respuesta.respuesta().contains("Línea 100"));
        verifyNoInteractions(gemini);
    }

    @Test
    void mensajeDePeligro_siempreResponde_aunqueYaSeHayaAlcanzadoElLimite() {
        // 5 mensajes de peligro con un límite de 2: ninguno debe ser bloqueado
        for (int i = 0; i < 5; i++) {
            assertTrue(servicio.responder("maria.l", pregunta("Estoy en peligro")).emergencia());
        }
    }

    @Test
    void alSuperarElLimite_lanzaLimiteYNoLlamaMasAlModelo() {
        when(gemini.generar(any(), anyList())).thenReturn("ok");

        servicio.responder("maria.l", pregunta("pregunta 1"));
        servicio.responder("maria.l", pregunta("pregunta 2"));

        assertThrows(LimiteMensajesException.class, () -> servicio.responder("maria.l", pregunta("pregunta 3")));
        verify(gemini, times(2)).generar(any(), anyList());
    }

    @Test
    void elLimiteEsPorUsuaria() {
        when(gemini.generar(any(), anyList())).thenReturn("ok");

        servicio.responder("maria.l", pregunta("1"));
        servicio.responder("maria.l", pregunta("2"));

        assertEquals("ok", servicio.responder("otra.usuaria", pregunta("1")).respuesta());
    }

    @Test
    void elNombreDeUsuarioNuncaSeEnviaAlModelo() {
        when(gemini.generar(any(), anyList())).thenReturn("ok");

        servicio.responder("maria.l", pregunta("hola"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GeminiClient.Turno>> captor = ArgumentCaptor.forClass(List.class);
        verify(gemini).generar(any(), captor.capture());
        assertFalse(captor.getValue().toString().contains("maria.l"));
    }

    // ---- construirTurnos: Gemini exige empezar con "user" y alternar roles ----

    private static TurnoRequest turno(String rol, String texto) {
        return new TurnoRequest(rol, texto);
    }

    @Test
    void sinHistorial_soloVaElMensajeActual() {
        assertEquals(List.of(new GeminiClient.Turno("user", "d")),
                AsistenteService.construirTurnos(null, "d"));
    }

    @Test
    void historialNormal_seConvierteYTerminaEnElMensajeActual() {
        var turnos = AsistenteService.construirTurnos(List.of(
                turno("usuaria", "hola"), turno("asistente", "buenas")), "¿y ahora?");

        assertEquals(List.of(
                new GeminiClient.Turno("user", "hola"),
                new GeminiClient.Turno("model", "buenas"),
                new GeminiClient.Turno("user", "¿y ahora?")), turnos);
    }

    @Test
    void historialQueEmpiezaConElAsistente_descartaEsosTurnosIniciales() {
        var turnos = AsistenteService.construirTurnos(List.of(
                turno("asistente", "bienvenida"), turno("usuaria", "a"), turno("asistente", "b")), "d");

        assertEquals("user", turnos.get(0).rol());
        assertEquals(List.of("a", "b", "d"), turnos.stream().map(GeminiClient.Turno::texto).toList());
    }

    @Test
    void rolesRepetidos_seQuedaElMasReciente() {
        var turnos = AsistenteService.construirTurnos(List.of(
                turno("usuaria", "a"), turno("usuaria", "a2"), turno("asistente", "b")), "d");

        assertEquals(List.of("a2", "b", "d"), turnos.stream().map(GeminiClient.Turno::texto).toList());
    }

    @Test
    void historialQueTerminaEnUsuaria_noDejaDosTurnosUserSeguidos() {
        var turnos = AsistenteService.construirTurnos(List.of(
                turno("usuaria", "a"), turno("asistente", "b"), turno("usuaria", "c")), "d");

        assertEquals(List.of("a", "b", "d"), turnos.stream().map(GeminiClient.Turno::texto).toList());
        for (int i = 1; i < turnos.size(); i++) {
            assertFalse(turnos.get(i).rol().equals(turnos.get(i - 1).rol()), "los roles deben alternar");
        }
    }

    @Test
    void disponible_dependeDeQueHayaClave() {
        when(gemini.configurado()).thenReturn(true);
        assertTrue(servicio.disponible());
        verify(gemini, never()).generar(any(), anyList());
    }
}
