package com.utp.proyectoriesgo.integration;

import com.jayway.jsonpath.JsonPath;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import com.utp.proyectoriesgo.service.ContactoService;
import com.utp.proyectoriesgo.service.DisponibilidadBorradoCuenta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnabledIfEnvironmentVariable(named = "SPRING_DATASOURCE_URL",
        matches = "jdbc:postgresql://127\\.0\\.0\\.1:[0-9]+/riesgo_seguridad_test")
@SpringBootTest(properties = {
        "app.jwt.secret-base64=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
class MiSeguridadPostgresTest {
    private static final String ACCIONES = "/api/mi/plan/acciones";
    private static final String LUGARES = "/api/mi/plan/lugares";
    private static final String CONTACTOS = "/api/mi/contactos";
    private static final String CUENTA = "/api/mi/cuenta";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsuarioRepository usuarias;

    @Autowired
    private JdbcClient jdbc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @MockitoSpyBean
    private DisponibilidadBorradoCuenta disponibilidad;

    @MockitoSpyBean
    private ContactoService contactos;

    @PersistenceContext
    private EntityManager entidades;

    @Test
    void datosPrivadosAisladosEntreDosCuentasConJwtReal() throws Exception {
        Sesion a = registrar();
        Sesion b = registrar();
        Recursos deA = crearRecursos(a);

        comprobarListas(b, 0);
        mvc.perform(patch(ACCIONES + "/{id}", deA.accion()).header(HttpHeaders.AUTHORIZATION, b.bearer())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"completada\":true}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete(ACCIONES + "/{id}", deA.accion()).header(HttpHeaders.AUTHORIZATION, b.bearer()))
                .andExpect(status().isNotFound());
        mvc.perform(delete(LUGARES + "/{id}", deA.lugar()).header(HttpHeaders.AUTHORIZATION, b.bearer()))
                .andExpect(status().isNotFound());
        mvc.perform(put(CONTACTOS + "/{id}", deA.contacto()).header(HttpHeaders.AUTHORIZATION, b.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Cambio ficticio\",\"telefono\":\"000000002\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete(CONTACTOS + "/{id}", deA.contacto()).header(HttpHeaders.AUTHORIZATION, b.bearer()))
                .andExpect(status().isNotFound());

        mvc.perform(patch(ACCIONES + "/{id}", deA.accion()).header(HttpHeaders.AUTHORIZATION, a.bearer())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"completada\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.completada").value(true));
        mvc.perform(put(CONTACTOS + "/{id}", deA.contacto()).header(HttpHeaders.AUTHORIZATION, a.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Cambio ficticio\",\"telefono\":\"000000002\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.telefono").value("000000002"));

        comprobarListas(a, 1);
        eliminarRecurso(ACCIONES, a, deA.accion());
        eliminarRecurso(LUGARES, a, deA.lugar());
        eliminarRecurso(CONTACTOS, a, deA.contacto());
        comprobarListas(a, 0);
    }

    @Test
    void borradoConservaDatosAjenosEInvalidaSesionesAlReutilizarElNombre() throws Exception {
        // Solo se sustituye el resultado del bloqueo para probar el recorrido transaccional.
        doReturn(true).when(disponibilidad).disponible();
        Sesion a = registrar();
        Sesion b = registrar();
        String segundoToken = iniciarSesion(a.usuaria().getUsername());
        crearRecursos(a);
        crearRecursos(b);

        mvc.perform(delete(CUENTA).header(HttpHeaders.AUTHORIZATION, a.bearer()))
                .andExpect(status().isNoContent());

        Long id = a.usuaria().getId();
        assertThat(usuarias.existsById(id)).isFalse();
        for (String tabla : new String[]{"usuarios_roles", "plan_acciones", "lugares_seguros", "contactos_confianza"}) {
            assertThat(jdbc.sql("select count(*) from " + tabla + " where usuario_id = :id")
                    .param("id", id).query(Long.class).single()).isZero();
        }
        comprobarListas(b, 1);

        mvc.perform(get("/api/asistente/estado").header(HttpHeaders.AUTHORIZATION, segundoToken))
                .andExpect(status().isUnauthorized());
        Sesion nueva = registrar(a.usuaria().getUsername());
        assertThat(nueva.usuaria().getId()).isNotEqualTo(id);
        crearRecursos(nueva);
        mvc.perform(get(ACCIONES).header(HttpHeaders.AUTHORIZATION, a.bearer()))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete(CUENTA).header(HttpHeaders.AUTHORIZATION, segundoToken))
                .andExpect(status().isUnauthorized());
        comprobarListas(nueva, 1);
    }

    @Test
    void revierteLaLimpiezaDeDatosSiFallaUnHook() throws Exception {
        doReturn(true).when(disponibilidad).disponible();
        Sesion sesion = registrar();
        crearRecursos(sesion);
        Long id = sesion.usuaria().getId();
        doAnswer(llamada -> {
            llamada.callRealMethod();
            entidades.flush();
            assertThat(jdbc.sql("select count(*) from contactos_confianza where usuario_id = :id")
                    .param("id", id).query(Long.class).single()).isZero();
            throw new IllegalStateException("Fallo simulado después de borrar los contactos");
        }).when(contactos).eliminarDatosDe(id);
        mvc.perform(delete(CUENTA).header(HttpHeaders.AUTHORIZATION, sesion.bearer()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("No se pudo eliminar la cuenta. Inténtalo de nuevo más tarde."));

        assertThat(usuarias.existsById(sesion.usuaria().getId())).isTrue();
        comprobarListas(sesion, 1);
    }

    @Test
    void bloqueoRealImpideBorrarYLosTokensPreviosALaMigracionSeRechazan() throws Exception {
        Sesion sesion = registrar();
        crearRecursos(sesion);
        mvc.perform(get(CUENTA + "/estado").header(HttpHeaders.AUTHORIZATION, sesion.bearer()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.disponible").value(false));
        mvc.perform(delete(CUENTA).header(HttpHeaders.AUTHORIZATION, sesion.bearer()))
                .andExpect(status().isServiceUnavailable());
        comprobarListas(sesion, 1);

        var claims = JwtClaimsSet.builder().issuer("riesgo-api").subject(sesion.usuaria().getUsername())
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(600))
                .claim("roles", java.util.List.of("ROLE_USER")).build();
        var tokenAnterior = jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims));
        mvc.perform(get(ACCIONES).header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAnterior.getTokenValue()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lasTresTablasProtegenLaRelacionConLaCuentaEnPostgres() {
        var relaciones = jdbc.sql("""
                select conname from pg_constraint
                where contype = 'f' and confrelid = 'usuarios'::regclass and confdeltype = 'a'
                """).query(String.class).list();
        assertThat(relaciones).contains("fk_plan_acciones_usuario", "fk_lugares_seguros_usuario",
                "fk_contactos_confianza_usuario");
    }

    private Sesion registrar() throws Exception {
        return registrar("test-" + UUID.randomUUID().toString().substring(0, 24));
    }

    private Sesion registrar(String username) throws Exception {
        MvcResult registro = mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"ClaveFicticia123","seudonimo":"Usuaria de prueba"}
                                """.formatted(username)))
                .andExpect(status().isCreated()).andReturn();
        String token = JsonPath.read(registro.getResponse().getContentAsString(), "$.accessToken");
        return new Sesion(usuarias.findByUsername(username).orElseThrow(), "Bearer " + token);
    }

    private String iniciarSesion(String username) throws Exception {
        MvcResult login = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"ClaveFicticia123"}
                                """.formatted(username)))
                .andExpect(status().isOk()).andReturn();
        return "Bearer " + JsonPath.<String>read(login.getResponse().getContentAsString(), "$.accessToken");
    }

    private Recursos crearRecursos(Sesion sesion) throws Exception {
        long accion = crear(ACCIONES, sesion, "{\"texto\":\"Acción ficticia\"}");
        long lugar = crear(LUGARES, sesion, "{\"nombre\":\"Lugar ficticio\"}");
        long contacto = crear(CONTACTOS, sesion, "{\"nombre\":\"Contacto ficticio\",\"telefono\":\"000000001\"}");
        return new Recursos(accion, lugar, contacto);
    }

    private long crear(String ruta, Sesion sesion, String datos) throws Exception {
        MvcResult respuesta = mvc.perform(post(ruta).header(HttpHeaders.AUTHORIZATION, sesion.bearer())
                        .contentType(MediaType.APPLICATION_JSON).content(datos))
                .andExpect(status().isCreated()).andReturn();
        Number id = JsonPath.read(respuesta.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    private void comprobarListas(Sesion sesion, int cantidad) throws Exception {
        for (String ruta : new String[]{ACCIONES, LUGARES, CONTACTOS}) {
            mvc.perform(get(ruta).header(HttpHeaders.AUTHORIZATION, sesion.bearer()))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(cantidad));
        }
    }

    private void eliminarRecurso(String ruta, Sesion sesion, long id) throws Exception {
        mvc.perform(delete(ruta + "/{id}", id).header(HttpHeaders.AUTHORIZATION, sesion.bearer()))
                .andExpect(status().isNoContent());
    }

    private record Sesion(Usuario usuaria, String bearer) {
    }

    private record Recursos(long accion, long lugar, long contacto) {
    }

}
