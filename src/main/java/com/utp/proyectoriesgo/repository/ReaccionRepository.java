package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.ReaccionPublicacion;
import com.utp.proyectoriesgo.model.TipoReaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReaccionRepository extends JpaRepository<ReaccionPublicacion, Long> {

    Optional<ReaccionPublicacion> findByUsuarioIdAndPublicacionIdAndTipo(Long usuarioId, Long publicacionId,
                                                                         TipoReaccion tipo);

    /** Filas [publicacionId, tipo, cantidad] de las publicaciones indicadas. */
    @Query("select r.publicacion.id, r.tipo, count(r) from ReaccionPublicacion r "
            + "where r.publicacion.id in :ids group by r.publicacion.id, r.tipo")
    List<Object[]> contar(@Param("ids") Collection<Long> ids);

    /** Filas [publicacionId, tipo] con lo que la usuaria ya reaccionó en esas publicaciones. */
    @Query("select r.publicacion.id, r.tipo from ReaccionPublicacion r "
            + "where r.usuarioId = :usuarioId and r.publicacion.id in :ids")
    List<Object[]> delasUsuaria(@Param("usuarioId") Long usuarioId, @Param("ids") Collection<Long> ids);

    /** Borra las reacciones de la usuaria y las que recibieron sus publicaciones. */
    @Modifying
    @Query("delete from ReaccionPublicacion r where r.usuarioId = :usuarioId "
            + "or r.publicacion.id in (select p.id from Publicacion p where p.autor.id = :usuarioId)")
    void eliminarDeUsuaria(@Param("usuarioId") Long usuarioId);
}
