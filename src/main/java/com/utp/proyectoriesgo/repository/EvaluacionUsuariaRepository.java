package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.EvaluacionUsuaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluacionUsuariaRepository extends JpaRepository<EvaluacionUsuaria, Long> {
    List<EvaluacionUsuaria> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    Optional<EvaluacionUsuaria> findByIdAndUsuarioId(Long id, Long usuarioId);

    /** Borra todas las evaluaciones de la usuaria (sus respuestas se borran antes con RespuestaEvaluacionRepository). */
    @Modifying
    @Query("delete from EvaluacionUsuaria e where e.usuarioId = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}
