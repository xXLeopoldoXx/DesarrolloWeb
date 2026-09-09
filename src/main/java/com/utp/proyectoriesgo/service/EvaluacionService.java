package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.EvaluacionRequest;
import com.utp.proyectoriesgo.exception.EvaluacionNoEncontradaException;
import com.utp.proyectoriesgo.model.Evaluacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.utp.proyectoriesgo.dto.ActualizarFactoresRequest;

@Service
public class EvaluacionService {

    private final Map<Long, Evaluacion> evaluaciones = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);
    private final RiesgoCalculador riesgoCalculador;

    public EvaluacionService(RiesgoCalculador riesgoCalculador) {
        this.riesgoCalculador = riesgoCalculador;
    }

    public List<Evaluacion> listar() {
        return new ArrayList<>(evaluaciones.values());
    }

    public Evaluacion buscarPorId(Long id) {
        Evaluacion evaluacion = evaluaciones.get(id);
        if (evaluacion == null) {
            throw new EvaluacionNoEncontradaException(id);
        }
        return evaluacion;
    }

    public List<Evaluacion> buscarPorTexto(String texto) {
        String busqueda = texto.toLowerCase();
        return evaluaciones.values().stream()
                .filter(e -> e.getSeudonimoVictima().toLowerCase().contains(busqueda)
                        || e.getRelacionAgresor().toLowerCase().contains(busqueda))
                .toList();
    }

    public Evaluacion crear(EvaluacionRequest request) {
        validar(request);
        Long id = secuencia.getAndIncrement();
        Evaluacion evaluacion = mapearDesdeRequest(id, request);

        riesgoCalculador.calcular(evaluacion);

        evaluaciones.put(id, evaluacion);
        return evaluacion;
    }

    public Evaluacion actualizar(Long id, EvaluacionRequest request) {
        validar(request);
        Evaluacion existente = buscarPorId(id);

        Evaluacion actualizada = mapearDesdeRequest(id, request);
        actualizada.setFechaRegistro(existente.getFechaRegistro());

        riesgoCalculador.calcular(actualizada);

        evaluaciones.put(id, actualizada);
        return actualizada;
    }

    public void eliminar(Long id) {
        Evaluacion eliminada = evaluaciones.remove(id);
        if (eliminada == null) {
            throw new EvaluacionNoEncontradaException(id);
        }
    }

    private Evaluacion mapearDesdeRequest(Long id, EvaluacionRequest request) {
        return new Evaluacion(
                id,
                request.seudonimoVictima(),
                request.edadVictima(),
                request.relacionAgresor(),
                request.amenazaMuerte(),
                request.violenciaFisicaPrevia(),
                request.estrangulamientoPrevio(),
                request.accesoArmas(),
                request.incumplimientoMedidaProteccion(),
                request.acosoOPersecucion(),
                request.separacionReciente(),
                request.controlExtremo(),
                request.consumoAlcoholDrogasAgresor(),
                request.denunciasPrevias(),
                request.hijosEnComun()
        );
    }

    public Evaluacion actualizarFactores(Long id, ActualizarFactoresRequest request) {
    Evaluacion existente = buscarPorId(id);

    existente.setAmenazaMuerte(request.amenazaMuerte());
    existente.setViolenciaFisicaPrevia(request.violenciaFisicaPrevia());
    existente.setEstrangulamientoPrevio(request.estrangulamientoPrevio());
    existente.setAccesoArmas(request.accesoArmas());
    existente.setIncumplimientoMedidaProteccion(request.incumplimientoMedidaProteccion());
    existente.setAcosoOPersecucion(request.acosoOPersecucion());
    existente.setSeparacionReciente(request.separacionReciente());
    existente.setControlExtremo(request.controlExtremo());
    existente.setConsumoAlcoholDrogasAgresor(request.consumoAlcoholDrogasAgresor());
    existente.setDenunciasPrevias(request.denunciasPrevias());
    existente.setHijosEnComun(request.hijosEnComun());

    riesgoCalculador.calcular(existente);
    return existente;
}

    private void validar(EvaluacionRequest request) {
        if (request.seudonimoVictima() == null || request.seudonimoVictima().isBlank()) {
            throw new IllegalArgumentException("El seudónimo de la víctima es obligatorio.");
        }
        if (request.edadVictima() == null || request.edadVictima() <= 0) {
            throw new IllegalArgumentException("La edad de la víctima debe ser un valor válido.");
        }
        if (request.relacionAgresor() == null || request.relacionAgresor().isBlank()) {
            throw new IllegalArgumentException("La relación con el agresor es obligatoria.");
        }
    }
}
