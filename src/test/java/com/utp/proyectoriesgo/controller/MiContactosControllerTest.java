package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.config.SecurityConfig;
import com.utp.proyectoriesgo.model.ContactoConfianza;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.ContactoConfianzaRepository;
import com.utp.proyectoriesgo.service.ContactoService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MiContactosController.class)
@Import({SecurityConfig.class, ContactoService.class})
class MiContactosControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UsuarioService usuarias;

    @MockitoBean
    private ContactoConfianzaRepository contactos;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void rechazaPeticionesSinSesion() throws Exception {
        mvc.perform(get("/api/mi/contactos")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void listaSoloContactosPropiosYDevuelve404ParaLosAjenos() throws Exception {
        Usuario usuaria = mock(Usuario.class);
        when(usuaria.getId()).thenReturn(2L);
        when(usuarias.actual(any())).thenReturn(usuaria);
        when(contactos.findByUsuarioIdOrderByIdAsc(2L))
                .thenReturn(List.of(new ContactoConfianza(2L, "Contacto B", "000000002", null)));

        mvc.perform(get("/api/mi/contactos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Contacto B"))
                .andExpect(jsonPath("$[0].usuarioId").doesNotExist());
        mvc.perform(put("/api/mi/contactos/10").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ejemplo\",\"telefono\":\"000000001\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/mi/contactos/10")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rechazaUnTelefonoInvalido() throws Exception {
        mvc.perform(post("/api/mi/contactos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ejemplo\",\"telefono\":\"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }
}
