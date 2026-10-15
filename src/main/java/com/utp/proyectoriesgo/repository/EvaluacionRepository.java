package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    // JPQL: se consulta la ENTIDAD Evaluacion y sus ATRIBUTOS Java (no la tabla ni
    // las columnas SQL).
    @Query("""
            SELECT e
            FROM Evaluacion e
            WHERE LOWER(e.seudonimoVictima) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(e.relacionAgresor) LIKE LOWER(CONCAT('%', :texto, '%'))
            ORDER BY e.seudonimoVictima
            """)
    List<Evaluacion> buscarPorTexto(@Param("texto") String texto);

    @Query("""
            SELECT e
            FROM Evaluacion e
            WHERE e.nivelRiesgo = :nivel
            ORDER BY e.puntajeRiesgo DESC
            """)
    List<Evaluacion> buscarPorNivelRiesgo(@Param("nivel") String nivel);

    @Query("""
            SELECT e
            FROM Evaluacion e
            WHERE e.fechaRegistro BETWEEN :desde AND :hasta
            ORDER BY e.fechaRegistro DESC
            """)
    List<Evaluacion> buscarPorRangoFecha(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}