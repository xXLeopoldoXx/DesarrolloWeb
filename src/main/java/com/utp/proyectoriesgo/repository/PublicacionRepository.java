
package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface PublicacionRepository
        extends JpaRepository<Publicacion, Long> {

    @EntityGraph(attributePaths = {"autor"})
    List<Publicacion> findByOcultaFalseOrderByFechaDesc();

}
