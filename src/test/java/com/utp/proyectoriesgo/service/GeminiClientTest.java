package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.AsistenteNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiClientTest {

    private static final String URL = "https://gemini.test/v1beta/models/modelo-x:generateContent";

    // Forma real de una respuesta de Gemini, con campos extra que el cliente debe ignorar
    private static final String RESPUESTA_OK = """
            {"candidates":[{"content":{"role":"model","parts":[{"text":"Hola, "},{"text":"¿en qué puedo ayudarte?"}]},
            "finishReason":"STOP","index":0}],"usageMetadata":{"promptTokenCount":5},"modelVersion":"modelo-x"}""";

    private MockRestServiceServer servidor;
    private GeminiClient cliente;

    @BeforeEach
    void preparar() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://gemini.test/v1beta");
        servidor = MockRestServiceServer.bindTo(builder).build();
        cliente = new GeminiClient(builder.build(), "modelo-x", "clave-de-prueba");
    }

    private List<GeminiClient.Turno> unaPregunta() {
        return List.of(new GeminiClient.Turno("user", "hola"));
    }

    @Test
    void envia_clave_en_cabecera_instrucciones_y_conversacion_y_devuelve_el_texto() {
        servidor.expect(requestTo(URL)) // la clave NO va en la URL
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("x-goog-api-key", "clave-de-prueba"))
                .andExpect(jsonPath("$.system_instruction.parts[0].text").value("instrucciones"))
                .andExpect(jsonPath("$.contents[0].role").value("user"))
                .andExpect(jsonPath("$.contents[0].parts[0].text").value("hola"))
                .andExpect(jsonPath("$.generationConfig.maxOutputTokens").value(GeminiClient.MAX_TOKENS_RESPUESTA))
                .andExpect(jsonPath("$.generationConfig.temperature").doesNotExist())
                .andRespond(withSuccess(RESPUESTA_OK, MediaType.APPLICATION_JSON));

        String respuesta = cliente.generar("instrucciones", unaPregunta());

        assertEquals("Hola, ¿en qué puedo ayudarte?", respuesta);
        servidor.verify();
    }

    @Test
    void conversacionDeVariosTurnosSeEnviaEnOrden() {
        servidor.expect(requestTo(URL))
                .andExpect(jsonPath("$.contents.length()").value(3))
                .andExpect(jsonPath("$.contents[1].role").value("model"))
                .andExpect(jsonPath("$.contents[2].parts[0].text").value("¿y después?"))
                .andRespond(withSuccess(RESPUESTA_OK, MediaType.APPLICATION_JSON));

        cliente.generar("i", List.of(
                new GeminiClient.Turno("user", "hola"),
                new GeminiClient.Turno("model", "buenas"),
                new GeminiClient.Turno("user", "¿y después?")));

        servidor.verify();
    }

    @Test
    void sinClave_noLlamaAGeminiYAvisaQueNoEstaDisponible() {
        var sinClave = new GeminiClient(RestClient.builder().baseUrl("https://gemini.test/v1beta").build(),
                "modelo-x", "  ");

        assertFalse(sinClave.configurado());
        var error = assertThrows(AsistenteNoDisponibleException.class,
                () -> sinClave.generar("i", unaPregunta()));
        assertTrue(error.getMessage().contains("Línea 100"));
    }

    @Test
    void conClave_estaConfigurado() {
        assertTrue(cliente.configurado());
    }

    @Test
    void erroresHttpDeGemini_seConviertenEnNoDisponible_sinFiltrarDetalles() {
        for (HttpStatus estado : List.of(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND,
                HttpStatus.TOO_MANY_REQUESTS, HttpStatus.INTERNAL_SERVER_ERROR)) {
            servidor.reset();
            servidor.expect(requestTo(URL))
                    .andRespond(withStatus(estado).contentType(MediaType.APPLICATION_JSON)
                            .body("{\"error\":{\"message\":\"detalle interno con clave-de-prueba\"}}"));

            var error = assertThrows(AsistenteNoDisponibleException.class,
                    () -> cliente.generar("i", unaPregunta()), "HTTP " + estado.value());

            assertFalse(error.getMessage().contains("clave-de-prueba"), "no debe filtrar la clave");
            assertFalse(error.getMessage().contains("detalle interno"));
        }
    }

    @Test
    void fallaDeRed_seConvierteEnNoDisponible() {
        servidor.expect(requestTo(URL)).andRespond(request -> {
            throw new IOException("Read timed out");
        });

        assertThrows(AsistenteNoDisponibleException.class, () -> cliente.generar("i", unaPregunta()));
    }

    @Test
    void respuestaSinCandidatos_esNoDisponible() {
        servidor.expect(requestTo(URL))
                .andRespond(withSuccess("{\"promptFeedback\":{\"blockReason\":\"SAFETY\"}}", MediaType.APPLICATION_JSON));

        assertThrows(AsistenteNoDisponibleException.class, () -> cliente.generar("i", unaPregunta()));
    }

    @Test
    void respuestaConTextoVacio_esNoDisponible() {
        servidor.expect(requestTo(URL))
                .andRespond(withSuccess(
                        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"  \"}]},\"finishReason\":\"SAFETY\"}]}",
                        MediaType.APPLICATION_JSON));

        assertThrows(AsistenteNoDisponibleException.class, () -> cliente.generar("i", unaPregunta()));
    }
}
