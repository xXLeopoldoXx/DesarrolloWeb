
package com.utp.productosapi.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.utp.productosapi.dto.ResumenBackendResponse;
import com.utp.productosapi.security.SecurityConfig;
import com.utp.productosapi.security.CustomUserDetailsService;
import com.utp.productosapi.service.AdminResumenService;
import com.utp.productosapi.service.ProductoService;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
@EnableWebSecurity
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminResumenService adminResumenService;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void adminPuedeConsultarResumen() throws Exception {

        when(adminResumenService.obtenerResumen())
            .thenReturn(new ResumenBackendResponse(
                13L, 6L, 4L, "BACKEND_OPERATIVO"
            ));

        mockMvc.perform(
            get("/api/admin/resumen")
                .with(jwt().authorities(
                    new SimpleGrantedAuthority("ROLE_ADMIN")
                ))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalProductos").value(13))
        .andExpect(jsonPath("$.estado")
            .value("BACKEND_OPERATIVO"));
    }

    @Test
    void usuarioNoPuedeConsultarResumen() throws Exception {

        mockMvc.perform(
            get("/api/admin/resumen")
                .with(jwt().authorities(
                    new SimpleGrantedAuthority("ROLE_USER")
                ))
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void sinTokenNoPuedeConsultarResumen() throws Exception {

        mockMvc.perform(
            get("/api/admin/resumen")
        )
        .andExpect(status().isUnauthorized());
    }
}

