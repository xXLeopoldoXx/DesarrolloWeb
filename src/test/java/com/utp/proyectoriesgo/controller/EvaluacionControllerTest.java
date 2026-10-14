package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.exception.EvaluacionNoEncontradaException;
import com.utp.proyectoriesgo.model.Evaluacion;
import com.utp.proyectoriesgo.service.EvaluacionService;
import com.utp.proyectoriesgo.config.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.Wit;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

import com.utp.proyectoriesgo.model.Seguimiento;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EvaluacionController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class EvaluacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluacionService service;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService

    private Evaluacion evaluacionEjemplo() {
        Evaluacion e = new Evaluacion();
        e.setId(1L);
        e.setSeudonimoVictima("Caso-Test");
        e.setEdadVictima(28);
        e.setRelacionAgresor("Pareja");
        e.setNivelRiesgo("ALTO");
        return e;
    }

    @Test
    void listar_debeRetornar200ConLista() throws Exception {
        when(service.listar()).thenReturn(List.of(evaluacionEjemplo()));

        mockMvc.perform(get("/api/evaluaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seudonimoVictima").value("Caso-Test"));
    }

    @Test
    void crear_debeRetornar201() throws Exception {
        when(service.crear(any())).thenReturn(evaluacionEjemplo());

        String json = """
                {
                  "seudonimoVictima": "Caso-Test",
                  "edadVictima": 28,
                  "relacionAgresor": "Pareja",
                  "amenazaMuerte": true,
                  "violenciaFisicaPrevia": false,
                  "estrangulamientoPrevio": false,
                  "accesoArmas": false,
                  "incumplimientoMedidaProteccion": false,
                  "acosoOPersecucion": false,
                  "separacionReciente": false,
                  "controlExtremo": false,
                  "consumoAlcoholDrogasAgresor": false,
                  "denunciasPrevias": false,
                  "hijosEnComun": false
                }
                """;

        mockMvc.perform(post("/api/evaluaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nivelRiesgo").value("ALTO"));
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(999L)).thenThrow(new EvaluacionNoEncontradaException(999L));

        mockMvc.perform(get("/api/evaluaciones/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarFactores_debeRetornar200() throws Exception {
        when(service.actualizarFactores(eq(1L), any())).thenReturn(evaluacionEjemplo());

        String json = """
                {
                  "amenazaMuerte": true,
                  "violenciaFisicaPrevia": true,
                  "estrangulamientoPrevio": false,
                  "accesoArmas": false,
                  "incumplimientoMedidaProteccion": false,
                  "acosoOPersecucion": false,
                  "separacionReciente": false,
                  "controlExtremo": false,
                  "consumoAlcoholDrogasAgresor": false,
                  "denunciasPrevias": false,
                  "hijosEnComun": false
                }
                """;

        mockMvc.perform(patch("/api/evaluaciones/1/factores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_debeRetornar204() throws Exception {
        mockMvc.perform(delete("/api/evaluaciones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void registrarSeguimiento_debeRetornar201() throws Exception {
        Seguimiento seguimiento = new Seguimiento(evaluacionEjemplo(), "Nueva amenaza", LocalDateTime.now());
        when(service.registrarSeguimiento(eq(1L), any())).thenReturn(seguimiento);

        mockMvc.perform(post("/api/evaluaciones/1/seguimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\": \"Nueva amenaza\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").value("Nueva amenaza"));
    }

    @Test
    void registrarSeguimiento_conNotaVacia_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/evaluaciones/1/seguimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\": \"\"}"))
                .andExpect(status().isBadRequest());
    }
}