package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.EntradaDiario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntradaDiarioRepository extends JpaRepository<EntradaDiario, Long> {

    List<EntradaDiario> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    Optional<EntradaDiario> findByIdAndUsuarioId(Long id, Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
