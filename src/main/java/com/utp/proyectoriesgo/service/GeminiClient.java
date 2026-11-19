package com.utp.proyectoriesgo.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.utp.proyectoriesgo.exception.AsistenteNoDisponibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Cliente mínimo de la API REST de Gemini (generateContent).
 *
 * - La clave viaja SOLO en la cabecera x-goog-api-key (nunca en la URL) y se lee de GEMINI_API_KEY.
 * - No se registra en logs ni el mensaje de la usuaria ni la respuesta: solo códigos de estado.
 * - No se envía "temperature": los modelos nuevos la ignoran y pronto responderán 400 si aparece.
 */
@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    /** Tope de tokens de la respuesta: mantiene las respuestas cortas y cuida la cuota gratuita. */
    static final int MAX_TOKENS_RESPUESTA = 500;

    private static final String MENSAJE_NO_DISPONIBLE =
            "El asistente no está disponible en este momento. Si necesitas ayuda ahora, "
                    + "llama a la Línea 100 (o al 105 si estás en peligro).";

    /** Un mensaje de la conversación. rol: "user" o "model" (nombres que usa Gemini). */
    public record Turno(String rol, String texto) {
    }

    private final RestClient http;
    private final String modelo;
    private final String apiKey;

    @Autowired
    public GeminiClient(
            @Value("${app.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}") String baseUrl,
            @Value("${app.gemini.model}") String modelo,
            @Value("${app.gemini.api-key:}") String apiKey,
            @Value("${app.gemini.timeout-seconds:20}") int timeoutSegundos) {
        this(construirRestClient(baseUrl, timeoutSegundos), modelo, apiKey);
    }

    // Para pruebas: permite inyectar un RestClient simulado
    GeminiClient(RestClient http, String modelo, String apiKey) {
        this.http = http;
        this.modelo = modelo;
        this.apiKey = apiKey == null ? "" : apiKey.trim();
    }

    private static RestClient construirRestClient(String baseUrl, int timeoutSegundos) {
        var cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        var fabrica = new JdkClientHttpRequestFactory(cliente);
        fabrica.setReadTimeout(Duration.ofSeconds(timeoutSegundos));
        return RestClient.builder().baseUrl(baseUrl).requestFactory(fabrica).build();
    }

    /** true si hay una clave configurada (no comprueba que sea válida). */
    public boolean configurado() {
        return !apiKey.isEmpty();
    }

    /**
     * Pide una respuesta al modelo.
     *
     * @param instrucciones instrucciones de sistema (el "rol" y las reglas del asistente)
     * @param turnos        conversación; debe empezar y terminar con un turno "user"
     * @throws AsistenteNoDisponibleException si no hay clave, Gemini falla o responde vacío
     */
    public String generar(String instrucciones, List<Turno> turnos) {
        if (!configurado()) {
            log.warn("Asistente sin configurar: falta la variable de entorno GEMINI_API_KEY");
            throw new AsistenteNoDisponibleException(MENSAJE_NO_DISPONIBLE);
        }

        Map<String, Object> cuerpo = Map.of(
                "system_instruction", Map.of("parts", List.of(Map.of("text", instrucciones))),
                "contents", turnos.stream()
                        .map(t -> Map.of("role", t.rol(), "parts", List.of(Map.of("text", t.texto()))))
                        .toList(),
                "generationConfig", Map.of("maxOutputTokens", MAX_TOKENS_RESPUESTA));

        try {
            RespuestaGemini respuesta = http.post()
                    .uri("/models/{modelo}:generateContent", modelo)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .body(RespuestaGemini.class);
            return extraerTexto(respuesta);
        } catch (RestClientResponseException e) {
            int estado = e.getStatusCode().value();
            log.warn("Gemini respondió HTTP {}{}", estado,
                    estado == 404 ? " (revisa el modelo: variable GEMINI_MODEL)" : "");
            throw new AsistenteNoDisponibleException(MENSAJE_NO_DISPONIBLE);
        } catch (RestClientException e) {
            log.warn("No se pudo contactar a Gemini ({})", e.getClass().getSimpleName());
            throw new AsistenteNoDisponibleException(MENSAJE_NO_DISPONIBLE);
        }
    }

    private String extraerTexto(RespuestaGemini respuesta) {
        if (respuesta == null || respuesta.candidates() == null || respuesta.candidates().isEmpty()) {
            // Sin candidatos: normalmente la petición fue bloqueada por filtros de seguridad
            log.warn("Gemini no devolvió candidatos");
            throw new AsistenteNoDisponibleException(MENSAJE_NO_DISPONIBLE);
        }

        Candidato candidato = respuesta.candidates().get(0);
        String texto = candidato.content() == null || candidato.content().parts() == null
                ? ""
                : candidato.content().parts().stream()
                        .map(Parte::text)
                        .filter(t -> t != null && !t.isBlank())
                        .reduce("", String::concat)
                        .trim();

        if (texto.isEmpty()) {
            log.warn("Gemini devolvió una respuesta vacía (finishReason={})", candidato.finishReason());
            throw new AsistenteNoDisponibleException(MENSAJE_NO_DISPONIBLE);
        }
        return texto;
    }

    // ---- Forma de la respuesta de Gemini (solo lo que usamos) ----

    @JsonIgnoreProperties(ignoreUnknown = true)
    record RespuestaGemini(List<Candidato> candidates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Candidato(Contenido content, String finishReason) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Contenido(List<Parte> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Parte(String text) {
    }
}
