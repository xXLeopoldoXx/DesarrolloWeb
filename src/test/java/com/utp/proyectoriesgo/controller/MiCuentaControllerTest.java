package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.exception.BorradoCuentaNoDisponibleException;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.CuentaService;
import com.utp.proyectoriesgo.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MiCuentaController.class)
@Import(SecurityConfig.class)
class MiCuentaControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CuentaService cuentas;

    @MockitoBean
    private UsuarioService usuarias;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final Usuario usuaria = new Usuario("cuenta-ficticia", "hash-de-prueba", true);

    @BeforeEach
    void configurarIdentidad() {
        when(usuarias.actual(any())).thenReturn(usuaria);
    }

    @Test
    void exigeSesionParaConsultarYEliminar() throws Exception {
        mvc.perform(get("/api/mi/cuenta/estado")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/mi/cuenta")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void informaElBloqueoYResponde503() throws Exception {
        doThrow(new BorradoCuentaNoDisponibleException()).when(cuentas).eliminarCuenta(usuaria);

        mvc.perform(get("/api/mi/cuenta/estado")).andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(false));
        mvc.perform(delete("/api/mi/cuenta")).andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.mensaje").value("La eliminación de cuenta aún no está disponible."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void soloEliminaLaCuentaDeLaSesion() throws Exception {
        when(cuentas.borradoDisponible()).thenReturn(true);

        mvc.perform(get("/api/mi/cuenta/estado")).andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(true));
        mvc.perform(delete("/api/mi/cuenta?usuarioId=99")).andExpect(status().isNoContent());
        verify(cuentas).eliminarCuenta(usuaria);
    }
}
