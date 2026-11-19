package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.model.LugarSeguro;
import com.utp.proyectoriesgo.model.PlanAccion;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.LugarSeguroRepository;
import com.utp.proyectoriesgo.repository.PlanAccionRepository;
import com.utp.proyectoriesgo.service.PlanSeguridadService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MiPlanController.class)
@Import({SecurityConfig.class, PlanSeguridadService.class})
class MiPlanControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UsuarioService usuarias;

    @MockitoBean
    private PlanAccionRepository acciones;

    @MockitoBean
    private LugarSeguroRepository lugares;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void rechazaPeticionesSinSesion() throws Exception {
        mvc.perform(get("/api/mi/plan/acciones")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void listaSoloLosRecursosDeLaUsuariaAutenticadaSinExponerPropiedad() throws Exception {
        autenticarComo(1L);
        when(acciones.findByUsuarioIdOrderByIdAsc(1L)).thenReturn(List.of(new PlanAccion(1L, "Acción A")));
        when(lugares.findByUsuarioIdOrderByIdAsc(1L)).thenReturn(List.of(new LugarSeguro(1L, "Lugar A", null, null)));

        mvc.perform(get("/api/mi/plan/acciones?usuarioId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].texto").value("Acción A"))
                .andExpect(jsonPath("$[0].usuarioId").doesNotExist());
        mvc.perform(get("/api/mi/plan/lugares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lugar A"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void noPermiteCambiarNiEliminarRecursosAjenos() throws Exception {
        autenticarComo(2L);

        mvc.perform(patch("/api/mi/plan/acciones/10")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"completada\":false}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/mi/plan/acciones/10")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/mi/plan/lugares/20")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rechazaTextoVacioYCompletadaOmitida() throws Exception {
        mvc.perform(post("/api/mi/plan/acciones")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\" \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/mi/plan/acciones/10")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    private void autenticarComo(Long id) {
        Usuario usuaria = mock(Usuario.class);
        when(usuaria.getId()).thenReturn(id);
        when(usuarias.actual(any())).thenReturn(usuaria);
    }
}
