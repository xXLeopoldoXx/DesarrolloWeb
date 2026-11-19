package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Comentario;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.Reporte;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.ComentarioService;
import com.utp.proyectoriesgo.service.PublicacionService;
import com.utp.proyectoriesgo.service.ReporteService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Comunidad (publicaciones, comentarios, reportes) y moderación: permisos, validación y privacidad. */
@WebMvcTest({PublicacionController.class, ComentarioController.class, ReporteController.class, ModeracionController.class})
@Import(SecurityConfig.class)
class ComunidadControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PublicacionService publicaciones;
    @MockitoBean
    private ComentarioService comentarios;
    @MockitoBean
    private ReporteService reportes;
    @MockitoBean
    private UsuarioService usuarias;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private UserDetailsService userDetailsService;

    private Usuario autor(String seudonimo) {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getSeudonimo()).thenReturn(seudonimo);
        when(usuario.getId()).thenReturn(7L);
        return usuario;
    }

    private Publicacion publicacion(Long id, Usuario autor, String texto) {
        Publicacion publicacion = mock(Publicacion.class);
        when(publicacion.getId()).thenReturn(id);
        when(publicacion.getAutor()).thenReturn(autor);
        when(publicacion.getTexto()).thenReturn(texto);
        when(publicacion.getFecha()).thenReturn(LocalDateTime.of(2026, 11, 19, 10, 0));
        return publicacion;
    }

    private ResultActions json(MockHttpServletRequestBuilder peticion, String cuerpo) throws Exception {
        return mvc.perform(peticion.contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    // ---------- Sin sesión ----------

    @Test
    void sinSesion_todoRespondeComo401() throws Exception {
        mvc.perform(get("/api/comunidad/publicaciones")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/comunidad/publicaciones/1/comentarios")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/comunidad/reportes")).andExpect(status().isUnauthorized());
        verifyNoInteractions(publicaciones, comentarios, reportes);
    }

    // ---------- Publicaciones ----------

    @Test
    @WithMockUser(roles = "USER")
    void listar_devuelveElSeudonimoYNuncaElUsuario() throws Exception {
        Publicacion conSeudonimo = publicacion(1L, autor("Luna"), "Hola a todas");
        Publicacion sinSeudonimo = publicacion(2L, autor(" "), "Sin seudónimo");
        when(publicaciones.listarPublicaciones()).thenReturn(List.of(conSeudonimo, sinSeudonimo));

        mvc.perform(get("/api/comunidad/publicaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seudonimo").value("Luna"))
                .andExpect(jsonPath("$[1].seudonimo").value("Anónima"))
                .andExpect(jsonPath("$[0].username").doesNotExist())
                .andExpect(jsonPath("$[0].autor").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "USER")
    void crear_devuelve201() throws Exception {
        Usuario usuaria = autor("Luna");
        Publicacion creada = publicacion(5L, usuaria, "Mi primer mensaje");
        when(usuarias.actual(any())).thenReturn(usuaria);
        when(publicaciones.crearPublicacion(eq("Mi primer mensaje"), eq(usuaria))).thenReturn(creada);

        json(post("/api/comunidad/publicaciones"), "{\"texto\":\"Mi primer mensaje\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.seudonimo").value("Luna"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void crear_conTextoVacioOMuyLargo_responde400() throws Exception {
        json(post("/api/comunidad/publicaciones"), "{\"texto\":\"   \"}").andExpect(status().isBadRequest());
        json(post("/api/comunidad/publicaciones"), "{\"texto\":\"" + "a".repeat(501) + "\"}")
                .andExpect(status().isBadRequest());
        verifyNoInteractions(publicaciones);
    }

    // ---------- Comentarios ----------

    @Test
    @WithMockUser(roles = "USER")
    void comentarios_deUnaPublicacionInexistenteOOculta_responde404() throws Exception {
        when(comentarios.listarComentarios(99L))
                .thenThrow(new RecursoNoEncontradoException("La publicación no existe"));

        mvc.perform(get("/api/comunidad/publicaciones/99/comentarios"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void comentar_devuelve201() throws Exception {
        Usuario usuaria = autor("Luna");
        Publicacion origen = publicacion(3L, autor("Otra"), "Texto");
        Comentario comentario = mock(Comentario.class);
        when(comentario.getId()).thenReturn(11L);
        when(comentario.getPublicacion()).thenReturn(origen);
        when(comentario.getAutor()).thenReturn(usuaria);
        when(comentario.getTexto()).thenReturn("Ánimo");
        when(comentario.getFecha()).thenReturn(LocalDateTime.of(2026, 11, 19, 11, 0));
        when(usuarias.actual(any())).thenReturn(usuaria);
        when(comentarios.crearComentario(eq(3L), eq("Ánimo"), eq(usuaria))).thenReturn(comentario);

        json(post("/api/comunidad/publicaciones/3/comentarios"), "{\"texto\":\"Ánimo\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.publicacionId").value(3))
                .andExpect(jsonPath("$.seudonimo").value("Luna"));
    }

    // ---------- Reportes y moderación ----------

    @Test
    @WithMockUser(roles = "USER")
    void reportar_devuelve201() throws Exception {
        Usuario usuaria = autor("Luna");
        Reporte reporte = mock(Reporte.class);
        when(reporte.getId()).thenReturn(4L);
        when(usuarias.actual(any())).thenReturn(usuaria);
        when(reportes.registrarReporte(eq(3L), eq("Lenguaje ofensivo"), eq(usuaria))).thenReturn(reporte);

        json(post("/api/comunidad/publicaciones/3/reportes"), "{\"motivo\":\"Lenguaje ofensivo\"}")
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void reportar_sinMotivo_responde400() throws Exception {
        json(post("/api/comunidad/publicaciones/3/reportes"), "{\"motivo\":\"\"}").andExpect(status().isBadRequest());
        verifyNoInteractions(reportes);
    }

    @Test
    @WithMockUser(roles = "USER")
    void unaUsuariaNormal_noPuedeModerar() throws Exception {
        mvc.perform(get("/api/admin/comunidad/reportes")).andExpect(status().isForbidden());
        mvc.perform(patch("/api/admin/comunidad/publicaciones/1/ocultar")).andExpect(status().isForbidden());
        verifyNoInteractions(publicaciones, reportes);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void admin_listaReportesSinRevelarQuienReporto() throws Exception {
        Reporte reporte = mock(Reporte.class);
        Publicacion origen = publicacion(8L, autor("Luna"), "Texto reportado");
        when(reporte.getId()).thenReturn(1L);
        when(reporte.getPublicacion()).thenReturn(origen);
        when(reporte.getMotivo()).thenReturn("Spam");
        when(reporte.getFecha()).thenReturn(LocalDateTime.of(2026, 11, 19, 12, 0));
        when(reportes.listarReportes()).thenReturn(List.of(reporte));

        mvc.perform(get("/api/admin/comunidad/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publicacionId").value(8))
                .andExpect(jsonPath("$[0].motivo").value("Spam"))
                .andExpect(jsonPath("$[0].autorPublicacion").value("Luna"))
                .andExpect(jsonPath("$[0].reportadaPor").doesNotExist())
                .andExpect(jsonPath("$[0].autorReporte").doesNotExist());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void admin_ocultaUnaPublicacion_204() throws Exception {
        mvc.perform(patch("/api/admin/comunidad/publicaciones/8/ocultar")).andExpect(status().isNoContent());
        verify(publicaciones).ocultarPublicacion(8L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void admin_ocultarUnaInexistente_404() throws Exception {
        doThrow(new RecursoNoEncontradoException("La publicación no existe"))
                .when(publicaciones).ocultarPublicacion(404L);

        mvc.perform(patch("/api/admin/comunidad/publicaciones/404/ocultar")).andExpect(status().isNotFound());
    }
}
