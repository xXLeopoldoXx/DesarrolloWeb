package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.model.Evaluacion;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EvaluacionService {

    private final Map<Long, Evaluacion> evaluaciones = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);
}