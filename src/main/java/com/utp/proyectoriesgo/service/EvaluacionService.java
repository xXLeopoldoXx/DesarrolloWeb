package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.*;
import com.utp.proyectoriesgo.dto.EvaluacionRequest;
import com.utp.proyectoriesgo.exception.EvaluacionNoEncontradaException;
import com.utp.proyectoriesgo.exception.ReglaNegocioException;
import com.utp.proyectoriesgo.model.Evaluacion;
import com.utp.proyectoriesgo.model.Seguimiento;
import com.utp.proyectoriesgo.repository.EvaluacionRepository;
import com.utp.proyectoriesgo.repository.SeguimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.utp.proyectoriesgo.dto.ReporteResponse;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EvaluacionService {

    private static final Set<String> NIVELES_VALIDOS = Set.of("BAJO", "MEDIO", "ALTO", "MUY_ALTO");

    private final EvaluacionRepository repository;
    private final SeguimientoRepository seguimientoRepository;
    private final RiesgoCalculador riesgoCalculador;

    public EvaluacionService(EvaluacionRepository repository,
            SeguimientoRepository seguimientoRepository,
            RiesgoCalculador riesgoCalculador) {
        this.repository = repository;
        this.seguimientoRepository = seguimientoRepository;
        this.riesgoCalculador = riesgoCalculador;
    }

    public EvaluacionService(EvaluacionRepository repository,
            RiesgoCalculador riesgoCalculador) {
        this(repository, null, riesgoCalculador);
    }

    // ---------- Consultas (solo lectura) ----------

    @Transactional(readOnly = true)
    public List<Evaluacion> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Evaluacion buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EvaluacionNoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<Evaluacion> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaNegocioException("El texto de búsqueda no puede estar vacío");
        }
        return repository.buscarPorTexto(texto.trim());
    }

    @Transactional(readOnly = true)
    public List<Evaluacion> buscarPorNivelRiesgo(String nivel) {
        String normalizado = nivel == null ? "" : nivel.trim().toUpperCase();
        if (!NIVELES_VALIDOS.contains(normalizado)) {
            throw new ReglaNegocioException(
                    "Nivel inválido. Use: BAJO, MEDIO, ALTO o MUY_ALTO");
        }
        return repository.buscarPorNivelRiesgo(normalizado);
    }

    @Transactional(readOnly = true)
    public List<Evaluacion> buscarPorRangoFecha(LocalDateTime desde, LocalDateTime hasta) {
        if (desde.isAfter(hasta)) {
            throw new ReglaNegocioException(
                    "La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        return repository.buscarPorRangoFecha(desde, hasta);
    }

    @Transactional(readOnly = true)
    public List<Seguimiento> listarSeguimientos(Long evaluacionId) {
        buscarPorId(evaluacionId); // lanza 404 si el caso no existe
        return seguimientoRepository.findByEvaluacionIdOrderByFechaDesc(evaluacionId);
    }

    @Transactional(readOnly = true)
    public ReporteResponse generarReporte() {
        Map<String, Long> porNivel = new LinkedHashMap<>();
        for (String nivel : List.of("BAJO", "MEDIO", "ALTO", "MUY_ALTO")) {
            porNivel.put(nivel, (long) repository.buscarPorNivelRiesgo(nivel).size());
        }
        return new ReporteResponse(LocalDateTime.now(), repository.count(), porNivel);
    }

    // ---------- Escrituras (transaccionales) ----------

    @Transactional
    public Evaluacion crear(EvaluacionRequest request) {
        validar(request);
        Evaluacion evaluacion = mapearDesdeRequest(null, request);
        riesgoCalculador.calcular(evaluacion);
        return repository.save(evaluacion);
    }

    @Transactional
    public Evaluacion actualizar(Long id, EvaluacionRequest request) {
        validar(request);
        Evaluacion existente = buscarPorId(id);

        Evaluacion actualizada = mapearDesdeRequest(id, request);
        actualizada.setFechaRegistro(existente.getFechaRegistro());
        actualizada.setFechaUltimoSeguimiento(existente.getFechaUltimoSeguimiento());
        riesgoCalculador.calcular(actualizada);

        return repository.save(actualizada);
    }

    @Transactional
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
        return repository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new EvaluacionNoEncontradaException(id);
        }
        seguimientoRepository.deleteByEvaluacionId(id); // primero los hijos (clave foránea)
        repository.deleteById(id);
    }

    /**
     * Transacción con DOS escrituras: guarda el seguimiento y actualiza el caso.
     * Si algo falla, se revierten las dos juntas (atomicidad).
     */
    @Transactional
    public Seguimiento registrarSeguimiento(Long evaluacionId, String nota) {
        validarNota(nota);
        Evaluacion evaluacion = buscarPorId(evaluacionId);
        LocalDateTime ahora = LocalDateTime.now();

        // Escritura 1: el caso queda administrado, Hibernate hace el UPDATE al
        // confirmar (dirty checking).
        evaluacion.setFechaUltimoSeguimiento(ahora);

        // Escritura 2: se inserta el seguimiento.
        return seguimientoRepository.save(new Seguimiento(evaluacion, nota.trim(), ahora));
    }

    /**
     * Hace exactamente lo mismo que registrarSeguimiento y luego lanza una
     * RuntimeException.
     * Resultado esperado: NADA queda guardado (ni el seguimiento ni el cambio en el
     * caso).
     */
    @Transactional
    public void simularErrorSeguimiento(Long evaluacionId, String nota) {
        registrarSeguimiento(evaluacionId, nota);
        throw new IllegalStateException("Error simulado: la transacción debe hacer rollback");
    }

    // ---------- Auxiliares ----------

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
                request.hijosEnComun());
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

    private void validarNota(String nota) {
        if (nota == null || nota.isBlank()) {
            throw new ReglaNegocioException("La nota del seguimiento es obligatoria");
        }
        if (nota.length() > 500) {
            throw new ReglaNegocioException("La nota no puede superar 500 caracteres");
        }
    }
}