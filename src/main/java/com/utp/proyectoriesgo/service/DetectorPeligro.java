package com.utp.proyectoriesgo.service;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/**
 * Detecta frases que indican peligro inmediato o riesgo para la vida.
 * Si coincide, el asistente responde con un texto fijo (105 y Línea 100) SIN llamar al modelo.
 *
 * Es una lista de frases claras, no un detector perfecto: puede dar falsos positivos (se prefiere
 * eso a no avisar) y no entiende frases muy indirectas. Para eso el prompt del modelo también
 * indica llamar a emergencias ante cualquier señal de peligro.
 */
@Component
public class DetectorPeligro {

    // Se comparan sin tildes y en minúsculas.
    private static final List<String> FRASES = List.of(
            // Peligro por violencia
            "me va a matar", "me quiere matar", "me quiere asesinar", "me va a asesinar",
            "me mate", "me matara", "me va a hacer algo", "me va a hacer dano", "me va a lastimar",
            "me va a pegar", "me va a golpear",
            "me amenazo de muerte", "me amenaza de muerte", "amenaza de muerte", "amenazas de muerte",
            "me estrangul", "me ahorc", "me intento matar", "intento matarme", "me esta golpeando",
            "me esta pegando", "me acaba de golpear", "me acaba de pegar", "tiene un arma",
            "tiene una pistola", "esta armado", "me esta siguiendo", "me persigue", "me secuestr",
            "no me deja salir", "me encerro", "me violo", "me esta violando",
            "estoy en peligro", "ayuda urgente", "necesito ayuda ya", "auxilio",
            // Riesgo para la propia vida
            "quiero morir", "quiero morirme", "me quiero matar", "quiero matarme", "suicid",
            "no quiero vivir", "acabar con mi vida", "quitarme la vida", "hacerme dano");

    public boolean indicaPeligro(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return false;
        }
        String texto = normalizar(mensaje);
        return FRASES.stream().anyMatch(texto::contains);
    }

    private static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        // Espacios múltiples → uno solo, para que "me   va a matar" también coincida
        return sinTildes.replaceAll("\\s+", " ").trim();
    }
}
