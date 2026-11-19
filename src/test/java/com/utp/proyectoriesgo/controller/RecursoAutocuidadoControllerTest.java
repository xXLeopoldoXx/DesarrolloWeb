package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.dto.RecursoAutocuidadoResponse;
import com.utp.proyectoriesgo.service.RecursoAutocuidadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecursoAutocuidadoController.class)
@Import(SecurityConfig.class)
class RecursoAutocuidadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecursoAutocuidadoService recursoAutocuidadoService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void recursosSinToken_responde401() throws Exception {
        mockMvc.perform(get("/api/mi/bienestar/recursos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "natty.a", roles = "USER")
    void listarRecursos_conSesion_devuelve200YLista() throws Exception {
        when(recursoAutocuidadoService.listar(any())).thenReturn(List.of(
                new RecursoAutocuidadoResponse(1L, "MINDFULNESS", "Respiración 4-7-8", "Descripción...", "EJERCICIO", "Paso a paso...")
        ));

        mockMvc.perform(get("/api/mi/bienestar/recursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoria").value("MINDFULNESS"))
                .andExpect(jsonPath("$[0].titulo").value("Respiración 4-7-8"));
    }
}
