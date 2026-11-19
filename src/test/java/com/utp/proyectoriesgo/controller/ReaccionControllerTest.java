package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.dto.ReaccionesResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.TipoReaccion;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.ReaccionService;
import com.utp.proyectoriesgo.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReaccionController.class)
@Import(SecurityConfig.class)
class ReaccionControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ReaccionService reacciones;
    @MockitoBean
    private UsuarioService usuarias;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void usuariaAutenticada() {
        Usuario usuaria = mock(Usuario.class);
        when(usuaria.getId()).thenReturn(7L);
        when(usuarias.actual(any())).thenReturn(usuaria);
    }

    private static String enviar(String tipo) {
        return "{\"tipo\":\"" + tipo + "\"}";
    }

    @Test
    void sinSesion_responde401_yNoLlegaAlServicio() throws Exception {
        mvc.perform(post("/api/comunidad/publicaciones/3/reacciones")
                        .contentType(MediaType.APPLICATION_JSON).content(enviar("ABRAZO")))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(reacciones);
    }

    @Test
    @WithMockUser(roles = "USER")
    void alternar_devuelveElEstadoFinalDeLasReacciones() throws Exception {
        Map<TipoReaccion, Long> conteos = new EnumMap<>(TipoReaccion.class);
        conteos.put(TipoReaccion.ABRAZO, 2L);
        conteos.put(TipoReaccion.FUERZA, 0L);
        conteos.put(TipoReaccion.IDENTIFICO, 0L);
        when(reacciones.alternar(7L, 3L, TipoReaccion.ABRAZO))
                .thenReturn(new ReaccionesResponse(conteos, List.of(TipoReaccion.ABRAZO)));

        mvc.perform(post("/api/comunidad/publicaciones/3/reacciones")
                        .contentType(MediaType.APPLICATION_JSON).content(enviar("ABRAZO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteos.ABRAZO").value(2))
                .andExpect(jsonPath("$.mias[0]").value("ABRAZO"));

        verify(reacciones).alternar(eq(7L), eq(3L), eq(TipoReaccion.ABRAZO));
    }

    @Test
    @WithMockUser(roles = "USER")
    void alternar_conTipoInvalidoOSinTipo_responde400() throws Exception {
        for (String cuerpo : List.of(enviar("ME_ENOJA"), "{}", "{\"tipo\":null}", "")) {
            mvc.perform(post("/api/comunidad/publicaciones/3/reacciones")
                            .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(reacciones);
    }

    @Test
    @WithMockUser(roles = "USER")
    void alternar_enUnaPublicacionInexistenteOOculta_responde404() throws Exception {
        when(reacciones.alternar(7L, 99L, TipoReaccion.FUERZA))
                .thenThrow(new RecursoNoEncontradoException("La publicación no existe"));

        mvc.perform(post("/api/comunidad/publicaciones/99/reacciones")
                        .contentType(MediaType.APPLICATION_JSON).content(enviar("FUERZA")))
                .andExpect(status().isNotFound());
    }
}
