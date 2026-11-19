package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.model.EvaluacionUsuaria;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.EvaluacionUsuariaRepository;
import com.utp.proyectoriesgo.repository.RespuestaEvaluacionRepository;
import com.utp.proyectoriesgo.service.AutoevaluacionService;
import com.utp.proyectoriesgo.service.CalculadoraAutoevaluacion;
import com.utp.proyectoriesgo.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MiEvaluacionController.class)
@Import({SecurityConfig.class, AutoevaluacionService.class, CalculadoraAutoevaluacion.class})
class MiEvaluacionControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UsuarioService usuarias;

    @MockitoBean
    private EvaluacionUsuariaRepository evaluaciones;

    @MockitoBean
    private RespuestaEvaluacionRepository respuestas;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private Usuario usuaria;

    @BeforeEach
    void usuariaAutenticada() {
        usuaria = mock(Usuario.class);
        when(usuaria.getId()).thenReturn(2L);
        when(usuarias.actual(any())).thenReturn(usuaria);
    }

    private static String cuerpoConTodasEn(String respuesta) {
        String[] factores = {"AMENAZA_MUERTE", "ACCESO_ARMAS", "ESTRANGULAMIENTO_PREVIO", "DENUNCIAS_PREVIAS",
                "INCUMPLIMIENTO_MEDIDA_PROTECCION", "ACOSO_O_PERSECUCION", "SEPARACION_RECIENTE",
                "CONTROL_EXTREMO", "CONSUMO_ALCOHOL_DROGAS_AGRESOR"};
        StringBuilder json = new StringBuilder("{\"respuestas\":[");
        for (int i = 0; i < factores.length; i++) {
            json.append(i == 0 ? "" : ",")
                    .append("{\"factor\":\"").append(factores[i]).append("\",\"respuesta\":\"")
                    .append(respuesta).append("\"}");
        }
        return json.append("]}").toString();
    }

    @Test
    void rechazaPeticionesSinSesion() throws Exception {
        mvc.perform(get("/api/mi/evaluaciones")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/mi/evaluaciones").contentType(MediaType.APPLICATION_JSON)
                .content(cuerpoConTodasEn("NO"))).andExpect(status().isUnauthorized());
        verify(evaluaciones, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void creaLaEvaluacionYDevuelve201ConElResultado() throws Exception {
        when(evaluaciones.save(any())).thenAnswer(llamada -> {
            EvaluacionUsuaria e = llamada.getArgument(0);
            ReflectionTestUtils.setField(e, "id", 11L);
            return e;
        });

        mvc.perform(post("/api/mi/evaluaciones").contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoConTodasEn("SI")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.nivel").value("MUY_ALTO"))
                .andExpect(jsonPath("$.puntaje").value(140))
                .andExpect(jsonPath("$.alertaCritica").value(true))
                .andExpect(jsonPath("$.senales.length()").value(9))
                .andExpect(jsonPath("$.porConfirmar").isEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rechazaUnaEvaluacionIncompletaORepetidaOConValoresInvalidos() throws Exception {
        String incompleta = "{\"respuestas\":[{\"factor\":\"AMENAZA_MUERTE\",\"respuesta\":\"SI\"}]}";
        String sinRespuestas = "{\"respuestas\":[]}";
        String valorInvalido = "{\"respuestas\":[{\"factor\":\"AMENAZA_MUERTE\",\"respuesta\":\"TAL_VEZ\"}]}";
        String sinFactor = "{\"respuestas\":[{\"respuesta\":\"SI\"}]}";

        for (String cuerpo : List.of(incompleta, sinRespuestas, valorInvalido, sinFactor)) {
            mvc.perform(post("/api/mi/evaluaciones").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                    .andExpect(status().isBadRequest());
        }
        verify(evaluaciones, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void listaSoloLasEvaluacionesDeLaUsuariaAutenticada() throws Exception {
        EvaluacionUsuaria propia = new EvaluacionUsuaria(2L, LocalDateTime.of(2026, 11, 19, 9, 30), 40, "MEDIO", false);
        ReflectionTestUtils.setField(propia, "id", 3L);
        when(evaluaciones.findByUsuarioIdOrderByFechaDesc(2L)).thenReturn(List.of(propia));

        mvc.perform(get("/api/mi/evaluaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].nivel").value("MEDIO"))
                .andExpect(jsonPath("$[0].usuarioId").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "USER")
    void laEvaluacionDeOtraUsuariaResponde404EnDetalleYEliminar() throws Exception {
        // La evaluación 10 es de otra usuaria: findByIdAndUsuarioId(10, 2) no la devuelve.
        when(evaluaciones.findByIdAndUsuarioId(10L, 2L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/mi/evaluaciones/10")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/mi/evaluaciones/10")).andExpect(status().isNotFound());
        verify(evaluaciones, never()).delete(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void eliminaUnaEvaluacionPropiaYDevuelve204() throws Exception {
        EvaluacionUsuaria propia = new EvaluacionUsuaria(2L, LocalDateTime.now(), 0, "BAJO", false);
        ReflectionTestUtils.setField(propia, "id", 4L);
        when(evaluaciones.findByIdAndUsuarioId(4L, 2L)).thenReturn(Optional.of(propia));

        mvc.perform(delete("/api/mi/evaluaciones/4")).andExpect(status().isNoContent());

        verify(evaluaciones).delete(propia);
    }
}
