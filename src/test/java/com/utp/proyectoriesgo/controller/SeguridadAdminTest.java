package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.dto.ReporteResponse;
import com.utp.proyectoriesgo.service.EvaluacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class SeguridadAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluacionService evaluacionService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void reporteSinToken_responde401() throws Exception {
        mockMvc.perform(get("/api/admin/reporte"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void reporteComoUser_responde403() throws Exception {
        mockMvc.perform(get("/api/admin/reporte"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reporteComoAdmin_responde200() throws Exception {
        when(evaluacionService.generarReporte()).thenReturn(new ReporteResponse(
                LocalDateTime.now(), 4L,
                Map.of("BAJO", 1L, "MEDIO", 1L, "ALTO", 1L, "MUY_ALTO", 1L)));

        mockMvc.perform(get("/api/admin/reporte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvaluaciones").value(4));
    }
}