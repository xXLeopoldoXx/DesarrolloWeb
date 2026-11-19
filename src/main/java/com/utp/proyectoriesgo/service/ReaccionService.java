package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.ReaccionesResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.ReaccionPublicacion;
import com.utp.proyectoriesgo.model.TipoReaccion;
import com.utp.proyectoriesgo.repository.PublicacionRepository;
import com.utp.proyectoriesgo.repository.ReaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reacciones de apoyo a las publicaciones. Sus datos se borran desde {@link ComunidadService}
 * (antes que las publicaciones, por las llaves foráneas).
 */
@Service
@Transactional(readOnly = true)
public class ReaccionService {

    private final ReaccionRepository reacciones;
    private final PublicacionRepository publicaciones;

    public ReaccionService(ReaccionRepository reacciones, PublicacionRepository publicaciones) {
        this.reacciones = reacciones;
        this.publicaciones = publicaciones;
    }

    /** Pone la reacción si la usuaria no la había dado y la quita si ya la había dado. Devuelve el estado final. */
    @Transactional
    public ReaccionesResponse alternar(Long usuarioId, Long publicacionId, TipoReaccion tipo) {
        Publicacion publicacion = publicaciones.findById(publicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La publicación no existe"));
        if (publicacion.isOculta()) {
            throw new RecursoNoEncontradoException("La publicación no está disponible");
        }

        Optional<ReaccionPublicacion> existente =
                reacciones.findByUsuarioIdAndPublicacionIdAndTipo(usuarioId, publicacionId, tipo);
        if (existente.isPresent()) {
            reacciones.delete(existente.get());
        } else {
            reacciones.save(new ReaccionPublicacion(usuarioId, publicacion, tipo));
        }

        return resumenes(usuarioId, List.of(publicacionId))
                .getOrDefault(publicacionId, ReaccionesResponse.vacia());
    }

    /** Totales por tipo y reacciones propias de varias publicaciones, con solo dos consultas. */
    public Map<Long, ReaccionesResponse> resumenes(Long usuarioId, Collection<Long> publicacionIds) {
        if (publicacionIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, Map<TipoReaccion, Long>> conteos = new HashMap<>();
        for (Object[] fila : reacciones.contar(publicacionIds)) {
            conteos.computeIfAbsent((Long) fila[0], id -> ceros())
                    .put((TipoReaccion) fila[1], (Long) fila[2]);
        }

        Map<Long, List<TipoReaccion>> propias = new HashMap<>();
        for (Object[] fila : reacciones.delasUsuaria(usuarioId, publicacionIds)) {
            propias.computeIfAbsent((Long) fila[0], id -> new ArrayList<>()).add((TipoReaccion) fila[1]);
        }

        Map<Long, ReaccionesResponse> resultado = new HashMap<>();
        for (Long id : publicacionIds) {
            resultado.put(id, new ReaccionesResponse(
                    conteos.getOrDefault(id, ceros()),
                    List.copyOf(propias.getOrDefault(id, List.of()))));
        }
        return resultado;
    }

    private static Map<TipoReaccion, Long> ceros() {
        Map<TipoReaccion, Long> mapa = new EnumMap<>(TipoReaccion.class);
        for (TipoReaccion tipo : TipoReaccion.values()) {
            mapa.put(tipo, 0L);
        }
        return mapa;
    }
}
