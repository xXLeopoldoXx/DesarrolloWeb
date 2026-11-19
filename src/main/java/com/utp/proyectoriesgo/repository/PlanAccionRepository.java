package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.PlanAccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanAccionRepository extends JpaRepository<PlanAccion, Long> {
    List<PlanAccion> findByUsuarioIdOrderByIdAsc(Long usuarioId);

    Optional<PlanAccion> findByIdAndUsuarioId(Long id, Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
