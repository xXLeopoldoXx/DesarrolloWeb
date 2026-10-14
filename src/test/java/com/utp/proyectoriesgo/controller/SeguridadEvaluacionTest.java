package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.service.EvaluacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({EvaluacionController.class, PublicoController.class})
@Import(SecurityConfig.class)
class SeguridadEvaluacionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluacionService evaluacionService;

    @Test
    void estadoPublico_sinLogin_responde200() throws Exception {
        mockMvc.perform(get("/api/publico/estado"))
                .andExpect(status().isOk());
    }

    @Test
    void listarSinAutenticacion_responde401() throws Exception {
        mockMvc.perform(get("/api/evaluaciones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void listarComoUser_responde200() throws Exception {
        mockMvc.perform(get("/api/evaluaciones"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void eliminarComoUser_responde403() throws Exception {
        mockMvc.perform(delete("/api/evaluaciones/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminarComoAdmin_noEsRechazadoPorSeguridad() throws Exception {
        // El service es un mock: lo que importa es que NO sea 401 ni 403
        mockMvc.perform(delete("/api/evaluaciones/1"))
                .andExpect(status().is2xxSuccessful());
    }
}