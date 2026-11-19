package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.LugarSeguro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LugarSeguroRepository extends JpaRepository<LugarSeguro, Long> {
    List<LugarSeguro> findByUsuarioIdOrderByIdAsc(Long usuarioId);

    Optional<LugarSeguro> findByIdAndUsuarioId(Long id, Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
