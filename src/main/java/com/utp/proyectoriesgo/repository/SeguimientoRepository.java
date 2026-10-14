package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

    // Consulta derivada: Spring interpreta el nombre del método.
    List<Seguimiento> findByEvaluacionIdOrderByFechaDesc(Long evaluacionId);

    // Necesario para poder eliminar un caso que ya tiene seguimientos (clave
    // foránea).
    void deleteByEvaluacionId(Long evaluacionId);
}