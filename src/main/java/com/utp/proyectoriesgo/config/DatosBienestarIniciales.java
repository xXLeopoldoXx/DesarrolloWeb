package com.utp.proyectoriesgo.config;

import com.utp.proyectoriesgo.model.RecursoAutocuidado;
import com.utp.proyectoriesgo.repository.RecursoAutocuidadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatosBienestarIniciales {

    @Bean
    CommandLineRunner cargarRecursosAutocuidado(RecursoAutocuidadoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new RecursoAutocuidado(
                                "MINDFULNESS",
                                "Respiración consciente 4-7-8",
                                "Técnica sencilla de respiración diafragmática para momentos de sobrecarga o angustia.",
                                "EJERCICIO",
                                "Inhala suavemente por la nariz contando 4 segundos, mantén el aire durante 7 segundos y exhala lentamente por la boca en 8 segundos. Repite 4 ciclos."
                        ),
                        new RecursoAutocuidado(
                                "EMOCIONAL",
                                "Escritura de desahogo y validación",
                                "Plasmar lo que sientes en tu diario personal privado sin juzgar tus pensamientos.",
                                "CONSEJO",
                                "Escribe con total libertad lo que estás experimentando hoy. Recuerda que no necesitas justificar tus emociones; sentirlas es el primer paso para procesarlas."
                        ),
                        new RecursoAutocuidado(
                                "EMOCIONAL",
                                "Establecimiento de límites saludables",
                                "Reconocer cuándo una situación o interacción sobrepasa tu paz mental.",
                                "CONSEJO",
                                "Decir 'no' o tomar distancia ante demandas que te causan malestar es un acto de cuidado personal, no de egoísmo. Tienes derecho a priorizar tu bienestar."
                        ),
                        new RecursoAutocuidado(
                                "FISICO",
                                "Liberación de tensión muscular",
                                "Atención a las zonas del cuerpo que acumulan mayor estrés físico.",
                                "EJERCICIO",
                                "Baja los hombros suavemente, relaja la mandíbula despegando la lengua del paladar y realiza giros lentos de cuello hacia ambos lados durante 2 minutos."
                        ),
                        new RecursoAutocuidado(
                                "SOCIAL",
                                "Conexión con tu red de confianza",
                                "Identificar personas seguras con quienes puedas hablar con tranquilidad.",
                                "CONSEJO",
                                "Mantén contacto frecuente con una amiga, familiar o persona de apoyo con quien te sientas escuchada y respetada sin ser juzgada."
                        ),
                        new RecursoAutocuidado(
                                "SEGURIDAD",
                                "Recursos y apoyo institucional MIMP",
                                "Orientación gratuita especializada y acompañamiento del Ministerio de la Mujer y Poblaciones Vulnerables.",
                                "ENLACE",
                                "https://www.gob.pe/mimp"
                        )
                ));
            }
        };
    }
}
