package com.utp.semana4_api_rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.utp.semana4_api_rest.security.TokenService;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    private String generarToken(String usuario, String rol) {
        var autenticacion = new UsernamePasswordAuthenticationToken(
                usuario,
                null,
                List.of(new SimpleGrantedAuthority(rol))
        );

        return tokenService.crearToken(autenticacion);
    }

    @Test
    void resumenSinTokenDebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/admin/resumen"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void resumenConRolUserDebeRetornar403() throws Exception {
        String token = generarToken("usuario-prueba", "ROLE_USER");

        mockMvc.perform(get("/api/admin/resumen")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void resumenConRolAdminDebeRetornar200() throws Exception {
        String token = generarToken("admin-prueba", "ROLE_ADMIN");

        mockMvc.perform(get("/api/admin/resumen")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }
}
