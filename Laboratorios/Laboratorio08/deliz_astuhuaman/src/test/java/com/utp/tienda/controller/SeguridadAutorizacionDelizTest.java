package com.utp.tienda.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.utp.tienda.config.SecurityConfig;
import com.utp.tienda.model.Producto;
import com.utp.tienda.security.CustomUserDetailsService;
import com.utp.tienda.service.ProductoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductoController.class, PublicoController.class})
@Import(SecurityConfig.class)
@DisplayName("Pruebas de Seguridad y Control de Acceso por Roles - Deliz Astuhuaman")
class SeguridadAutorizacionDelizTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("01. Endpoint público debe responder 200 OK sin autenticación")
    void test01_debePermitirAccesoSinCredencialesAEndpointPublico() throws Exception {
        mockMvc.perform(get("/api/publico/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("API disponible"));
    }

    @Test
    @DisplayName("02. Petición anónima a productos debe ser rechazada con 401 Unauthorized")
    void test02_debeRechazarCon401AccesoAnonimoAProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "deliz_user", roles = {"USER"})
    @DisplayName("03. Usuario autenticado con ROLE_USER debe listar productos con 200 OK")
    void test03_debePermitirListarProductosAUsuarioConRoleUser() throws Exception {
        when(productoService.listar()).thenReturn(List.of(
                new Producto("Teclado Mecanico", "Perifericos", new BigDecimal("250.00"), 15)
        ));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "deliz_user", roles = {"USER"})
    @DisplayName("04. Usuario con solo ROLE_USER no puede eliminar productos: 403 Forbidden")
    void test04_debeRechazarCon403CuandoUserIntentaEliminar() throws Exception {
        mockMvc.perform(delete("/api/productos/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "deliz_user", roles = {"USER"})
    @DisplayName("05. Usuario con solo ROLE_USER no puede crear productos: 403 Forbidden")
    void test05_debeRechazarCon403CuandoUserIntentaCrearProducto() throws Exception {
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nombre": "Monitor Gamer 144Hz",
                        "categoria": "Monitores",
                        "precio": 890.00,
                        "stock": 5
                    }
                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "deliz_admin", roles = {"ADMIN"})
    @DisplayName("06. Administrador con ROLE_ADMIN puede crear productos: 201 Created")
    void test06_debePermitirCrearAUsuarioConRoleAdmin() throws Exception {
        Producto creado = new Producto("Monitor Gamer 144Hz", "Monitores", new BigDecimal("890.00"), 5);
        creado.setId(1L);
        when(productoService.crear(any(Producto.class))).thenReturn(creado);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nombre": "Monitor Gamer 144Hz",
                        "categoria": "Monitores",
                        "precio": 890.00,
                        "stock": 5
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Monitor Gamer 144Hz"));
    }

    @Test
    @WithMockUser(username = "deliz_user", roles = {"USER"})
    @DisplayName("07. Ajustes de inventario reservados a ROLE_ADMIN deben retornar 403 para ROLE_USER")
    void test07_debeRechazarCon403ModuloInventarioAjustesSinAdmin() throws Exception {
        mockMvc.perform(post("/api/inventario/ajustes"))
                .andExpect(status().isForbidden());
    }
}
