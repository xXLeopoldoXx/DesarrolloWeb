package com.utp.semana4_api_rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.utp.semana4_api_rest.security.TokenService;
import com.utp.semana4_api_rest.model.Producto;
import com.utp.semana4_api_rest.repository.MovimientoStockRepository;
import com.utp.semana4_api_rest.repository.ProductoRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ProductoRepository productos;

    @Autowired
    private MovimientoStockRepository movimientos;

    private static final String SALIDA = "/api/inventario/{id}/salida";

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

    @Test
    void salidaRequiereAutenticacionYRolAdmin() throws Exception {
        Long id = productos.save(new Producto(null, "Mouse de prueba", "Pruebas", 40, 10)).getId();
        long movimientosAntes = movimientos.count();

        mockMvc.perform(post(SALIDA, id).param("cantidad", "3"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post(SALIDA, id).param("cantidad", "3")
                        .header(HttpHeaders.AUTHORIZATION, bearer("ROLE_USER")))
                .andExpect(status().isForbidden());

        assertEquals(10, productos.findById(id).orElseThrow().getStock());
        assertEquals(movimientosAntes, movimientos.count());
    }

    @Test
    void salidaVerificaCantidadYCambiaStockYMovimientoSoloAlAcertar() throws Exception {
        Long id = productos.save(new Producto(null, "Monitor de prueba", "Pruebas", 250, 10)).getId();
        long movimientosAntes = movimientos.count();
        String token = bearer("ROLE_ADMIN");

        mockMvc.perform(post(SALIDA, id).param("cantidad", "0")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(SALIDA, id).param("cantidad", "1.5")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(SALIDA, id).param("cantidad", "11")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isConflict());
        mockMvc.perform(post(SALIDA, Long.MAX_VALUE).param("cantidad", "1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound());

        assertEquals(10, productos.findById(id).orElseThrow().getStock());
        assertEquals(movimientosAntes, movimientos.count());

        mockMvc.perform(post(SALIDA, id).param("cantidad", "3")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(7));

        assertEquals(7, productos.findById(id).orElseThrow().getStock());
        assertEquals(movimientosAntes + 1, movimientos.count());
    }

    private String bearer(String rol) {
        return "Bearer " + generarToken("cuenta-ficticia", rol);
    }
}
