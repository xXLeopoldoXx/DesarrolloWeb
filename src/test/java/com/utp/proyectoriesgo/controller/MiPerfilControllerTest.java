package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.UsuarioService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MiPerfilController.class)
@Import(SecurityConfig.class)
class MiPerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void perfilSinToken_responde401() throws Exception {
        mockMvc.perform(get("/api/mi/perfil"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "maria.l", roles = "USER")
    void perfilConSesion_devuelveSeudonimoYRoles() throws Exception {
        Usuario usuario = new Usuario("maria.l", "hash", true);
        usuario.setSeudonimo("Luna");
        when(usuarioService.actual(any())).thenReturn(usuario);

        mockMvc.perform(get("/api/mi/perfil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maria.l"))
                .andExpect(jsonPath("$.seudonimo").value("Luna"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }
}
