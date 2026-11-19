package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.RespuestaEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RespuestaEvaluacionRepository extends JpaRepository<RespuestaEvaluacion, Long> {
    List<RespuestaEvaluacion> findByEvaluacionId(Long evaluacionId);

    @Modifying
    @Query("delete from RespuestaEvaluacion r where r.evaluacionId in :ids")
    void deleteByEvaluacionIdIn(@Param("ids") Collection<Long> ids);
}
