package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.DiarioRequest;
import com.utp.proyectoriesgo.dto.DiarioResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.EntradaDiario;
import com.utp.proyectoriesgo.repository.EntradaDiarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiarioService implements LimpiezaDatosUsuaria {

    private final EntradaDiarioRepository entradaDiarioRepository;

    public DiarioService(EntradaDiarioRepository entradaDiarioRepository) {
        this.entradaDiarioRepository = entradaDiarioRepository;
    }

    @Transactional(readOnly = true)
    public List<DiarioResponse> listar(Long usuarioId) {
        return entradaDiarioRepository.findByUsuarioIdOrderByFechaDesc(usuarioId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DiarioResponse obtenerPorId(Long id, Long usuarioId) {
        EntradaDiario entrada = entradaDiarioRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrada de diario no encontrada"));
        return mapearAResponse(entrada);
    }

    @Transactional
    public DiarioResponse crear(Long usuarioId, DiarioRequest request) {
        EntradaDiario entrada = new EntradaDiario(
                usuarioId,
                request.titulo().trim(),
                LocalDateTime.now(),
                request.estadoAnimo(),
                request.emocion() != null ? request.emocion().trim() : null,
                request.contenido().trim()
        );

        EntradaDiario guardada = entradaDiarioRepository.save(entrada);
        return mapearAResponse(guardada);
    }

    @Transactional
    public DiarioResponse actualizar(Long id, Long usuarioId, DiarioRequest request) {
        EntradaDiario entrada = entradaDiarioRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrada de diario no encontrada"));

        entrada.setTitulo(request.titulo().trim());
        entrada.setEstadoAnimo(request.estadoAnimo());
        entrada.setEmocion(request.emocion() != null ? request.emocion().trim() : null);
        entrada.setContenido(request.contenido().trim());

        EntradaDiario guardada = entradaDiarioRepository.save(entrada);
        return mapearAResponse(guardada);
    }

    @Transactional
    public void eliminar(Long id, Long usuarioId) {
        EntradaDiario entrada = entradaDiarioRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrada de diario no encontrada"));
        entradaDiarioRepository.delete(entrada);
    }

    /**
     * Implementación de LimpiezaDatosUsuaria para la opción "Eliminar mis datos" (privacidad).
     * Borra todas las entradas asociadas a la usuaria de forma idempotente.
     */
    @Override
    @Transactional
    public void eliminarDatosDe(Long usuarioId) {
        entradaDiarioRepository.deleteByUsuarioId(usuarioId);
    }

    private DiarioResponse mapearAResponse(EntradaDiario e) {
        return new DiarioResponse(
                e.getId(),
                e.getUsuarioId(),
                e.getTitulo(),
                e.getFecha(),
                e.getEstadoAnimo(),
                e.getEmocion(),
                e.getContenido()
        );
    }
}
