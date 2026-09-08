package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.EvaluacionNoEncontradaException;
import com.utp.proyectoriesgo.model.Evaluacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EvaluacionService {

    private final Map<Long, Evaluacion> evaluaciones = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);

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
}
