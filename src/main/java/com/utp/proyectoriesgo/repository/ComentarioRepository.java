
package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.Comentario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComentarioRepository
        extends JpaRepository<Comentario, Long> {

    @EntityGraph(attributePaths = {"autor"})
    List<Comentario> findByPublicacionIdAndOcultoFalseOrderByFechaAsc(
            Long publicacionId
    );

    /** Borra los comentarios de la usuaria y los que recibieron sus publicaciones. */
    @Modifying
    @Query("delete from Comentario c where c.autor.id = :usuarioId "
            + "or c.publicacion.id in (select p.id from Publicacion p where p.autor.id = :usuarioId)")
    void eliminarDeUsuaria(@Param("usuarioId") Long usuarioId);
}
