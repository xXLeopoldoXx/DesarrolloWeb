
package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface PublicacionRepository
        extends JpaRepository<Publicacion, Long> {

    @EntityGraph(attributePaths = {"autor"})
    List<Publicacion> findByOcultaFalseOrderByFechaDesc();

    @Modifying
    @Query("delete from Publicacion p where p.autor.id = :usuarioId")
    void eliminarDeUsuaria(@Param("usuarioId") Long usuarioId);

}
