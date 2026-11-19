package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.AutoevaluacionRequest;
import com.utp.proyectoriesgo.dto.RespuestaItem;
import com.utp.proyectoriesgo.dto.ResultadoResponse;
import com.utp.proyectoriesgo.dto.ResumenEvaluacionResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.exception.ReglaNegocioException;
import com.utp.proyectoriesgo.model.EvaluacionUsuaria;
import com.utp.proyectoriesgo.model.Factor;
import com.utp.proyectoriesgo.model.RespuestaEvaluacion;
import com.utp.proyectoriesgo.repository.EvaluacionUsuariaRepository;
import com.utp.proyectoriesgo.repository.RespuestaEvaluacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * «Mi evaluación»: cada usuaria solo ve y borra sus propias evaluaciones (una ajena responde 404, igual
 * que una inexistente). El nombre del bean debe ser «autoevaluacionService»: DisponibilidadBorradoCuenta
 * lo exige para habilitar «Eliminar mi cuenta».
 */
@Service
@Transactional(readOnly = true)
public class AutoevaluacionService implements LimpiezaDatosUsuaria {
    private final EvaluacionUsuariaRepository evaluaciones;
    private final RespuestaEvaluacionRepository respuestas;
    private final CalculadoraAutoevaluacion calculadora;

    public AutoevaluacionService(EvaluacionUsuariaRepository evaluaciones,
                                 RespuestaEvaluacionRepository respuestas,
                                 CalculadoraAutoevaluacion calculadora) {
        this.evaluaciones = evaluaciones;
        this.respuestas = respuestas;
        this.calculadora = calculadora;
    }

    @Transactional
    public ResultadoResponse crear(Long usuarioId, AutoevaluacionRequest solicitud) {
        validarCompleta(solicitud.respuestas());
        ResultadoResponse calculado = calculadora.calcular(solicitud.respuestas());

        EvaluacionUsuaria guardada = evaluaciones.save(new EvaluacionUsuaria(usuarioId, LocalDateTime.now(),
                calculado.puntaje(), calculado.nivel(), calculado.alertaCritica()));
        respuestas.saveAll(solicitud.respuestas().stream()
                .map(item -> new RespuestaEvaluacion(guardada.getId(), item.factor(), item.respuesta()))
                .toList());

        return calculado.conIdentidad(guardada.getId(), guardada.getFecha());
    }

    public List<ResumenEvaluacionResponse> listar(Long usuarioId) {
        return evaluaciones.findByUsuarioIdOrderByFechaDesc(usuarioId).stream()
                .map(e -> new ResumenEvaluacionResponse(e.getId(), e.getFecha(), e.getNivel()))
                .toList();
    }

    public ResultadoResponse detalle(Long usuarioId, Long id) {
        EvaluacionUsuaria evaluacion = buscar(usuarioId, id);
        List<RespuestaItem> items = respuestas.findByEvaluacionId(id).stream()
                .map(r -> new RespuestaItem(r.getFactor(), r.getRespuesta()))
                .toList();
        ResultadoResponse textos = calculadora.calcular(items);

        // Puntaje, nivel y alerta son los que se guardaron al evaluar; solo los textos se reconstruyen.
        return new ResultadoResponse(evaluacion.getId(), evaluacion.getFecha(), evaluacion.getNivel(),
                evaluacion.getPuntaje(), evaluacion.isAlertaCritica(), textos.senales(), textos.porConfirmar());
    }

    @Transactional
    public void eliminar(Long usuarioId, Long id) {
        EvaluacionUsuaria evaluacion = buscar(usuarioId, id);
        respuestas.deleteByEvaluacionIdIn(List.of(id));
        evaluaciones.delete(evaluacion);
    }

    @Override
    @Transactional
    public void eliminarDatosDe(Long usuarioId) {
        List<Long> ids = evaluaciones.findByUsuarioIdOrderByFechaDesc(usuarioId).stream()
                .map(EvaluacionUsuaria::getId)
                .toList();
        if (!ids.isEmpty()) {
            respuestas.deleteByEvaluacionIdIn(ids);
        }
        evaluaciones.deleteByUsuarioId(usuarioId);
    }

    private EvaluacionUsuaria buscar(Long usuarioId, Long id) {
        return evaluaciones.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evaluación no encontrada."));
    }

    /** Una respuesta por cada uno de los 9 factores: faltar alguno bajaría el puntaje sin que ella lo sepa. */
    private static void validarCompleta(List<RespuestaItem> items) {
        Set<Factor> vistos = EnumSet.noneOf(Factor.class);
        for (RespuestaItem item : items) {
            if (!vistos.add(item.factor())) {
                throw new ReglaNegocioException("Cada pregunta se responde una sola vez.");
            }
        }
        if (vistos.size() != Factor.values().length) {
            throw new ReglaNegocioException("Responde las " + Factor.values().length + " preguntas del cuestionario.");
        }
    }
}
