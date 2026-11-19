
package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface ReporteRepository
        extends JpaRepository<Reporte, Long> {

    boolean existsByPublicacionIdAndAutorId(
            Long publicacionId,
            Long autorId
    );

    @EntityGraph(attributePaths = {
        "publicacion",
        "publicacion.autor",
        "autor"
    })
    List<Reporte> findAllByOrderByFechaDesc();
}
