package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.exception.UsuarioExistenteException;
import com.utp.proyectoriesgo.security.TokenService;
import com.utp.proyectoriesgo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void loginValido_devuelveToken() throws Exception {
        var auth = UsernamePasswordAuthenticationToken.authenticated(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.crearToken(auth)).thenReturn("token-falso");
        when(tokenService.expiresInSeconds()).thenReturn(1800L);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("token-falso"))
                .andExpect(jsonPath("$.expiresInSeconds").value(1800));
    }

    @Test
    void loginConClaveIncorrecta_responde401() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("mala"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConCamposVacios_responde400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    private static final String REGISTRO_VALIDO =
            "{\"username\":\"maria.l\",\"password\":\"Clave1234\",\"seudonimo\":\"Luna\"}";

    @Test
    void registroValido_sinToken_responde201ConToken() throws Exception {
        var auth = UsernamePasswordAuthenticationToken.authenticated(
                "maria.l", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.crearToken(auth)).thenReturn("token-nuevo");
        when(tokenService.expiresInSeconds()).thenReturn(1800L);

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTRO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("token-nuevo"));
    }

    @Test
    void registroConUsuarioRepetido_responde409() throws Exception {
        when(usuarioService.registrar(any()))
                .thenThrow(new UsuarioExistenteException("Ese nombre de usuario ya está en uso"));

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTRO_VALIDO))
                .andExpect(status().isConflict());
    }

    @Test
    void registroConClaveDebil_responde400() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"maria.l\",\"password\":\"corta\",\"seudonimo\":\"Luna\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registroConUsuarioConMayusculas_responde400() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Maria\",\"password\":\"Clave1234\",\"seudonimo\":\"Luna\"}"))
                .andExpect(status().isBadRequest());
    }

    // CORS: el navegador pregunta con OPTIONS antes de enviar el POST desde Angular (localhost:4200).
    @Test
    void preflightDesdeAngular_esAceptado() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void preflightDesdeOtroOrigen_esRechazado() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", "http://sitio-malicioso.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    @Test
    void meSinToken_responde401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meConToken_devuelveUsuarioYRoles() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .with(jwt().jwt(j -> j.subject("usuario"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("usuario"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }
}