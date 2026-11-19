package com.utp.proyectoriesgo.controller;

import tools.jackson.databind.ObjectMapper;
import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.dto.DiarioRequest;
import com.utp.proyectoriesgo.dto.DiarioResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.DiarioService;
import com.utp.proyectoriesgo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MiDiarioController.class)
@Import(SecurityConfig.class)
class MiDiarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DiarioService diarioService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    // Se crea una sola vez (no dentro de when(...)) para no anidar stubbing de Mockito.
    private final Usuario usuaria = crearUsuaria();

    private static Usuario crearUsuaria() {
        Usuario u = mock(Usuario.class);
        when(u.getId()).thenReturn(5L);
        return u;
    }

    @Test
    void diarioSinToken_responde401() throws Exception {
        mockMvc.perform(get("/api/mi/diario"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "natty.a", roles = "USER")
    void listar_conSesion_devuelve200YLista() throws Exception {
        when(usuarioService.actual(any())).thenReturn(usuaria);
        when(diarioService.listar(5L)).thenReturn(List.of(
                new DiarioResponse(1L, 5L, "Mi día", LocalDateTime.now(), 4, "Tranquila", "Todo bien.")
        ));

        mockMvc.perform(get("/api/mi/diario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Mi día"))
                .andExpect(jsonPath("$[0].estadoAnimo").value(4));
    }

    @Test
    @WithMockUser(username = "natty.a", roles = "USER")
    void crear_conDatosValidos_devuelve201() throws Exception {
        when(usuarioService.actual(any())).thenReturn(usuaria);
        DiarioRequest req = new DiarioRequest("Mi entrada", 5, "Calma", "Texto privado");
        DiarioResponse res = new DiarioResponse(10L, 5L, "Mi entrada", LocalDateTime.now(), 5, "Calma", "Texto privado");

        when(diarioService.crear(eq(5L), any(DiarioRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/mi/diario")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/mi/diario/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Mi entrada"));
    }

    @Test
    @WithMockUser(username = "natty.a", roles = "USER")
    void obtenerPorId_siEsDeOtraUsuaria_responde404() throws Exception {
        when(usuarioService.actual(any())).thenReturn(usuaria);
        when(diarioService.obtenerPorId(eq(99L), eq(5L)))
                .thenThrow(new RecursoNoEncontradoException("Entrada de diario no encontrada"));

        mockMvc.perform(get("/api/mi/diario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "natty.a", roles = "USER")
    void eliminar_siEsDeOtraUsuaria_responde404() throws Exception {
        when(usuarioService.actual(any())).thenReturn(usuaria);
        doThrow(new RecursoNoEncontradoException("Entrada de diario no encontrada"))
                .when(diarioService).eliminar(eq(99L), eq(5L));

        mockMvc.perform(delete("/api/mi/diario/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
