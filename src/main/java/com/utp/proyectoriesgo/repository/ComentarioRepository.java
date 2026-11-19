
package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.Comentario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository
        extends JpaRepository<Comentario, Long> {

    @EntityGraph(attributePaths = {"autor"})
    List<Comentario> findByPublicacionIdAndOcultoFalseOrderByFechaAsc(
            Long publicacionId
    );
}
