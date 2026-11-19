package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.RecursoAutocuidado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecursoAutocuidadoRepository extends JpaRepository<RecursoAutocuidado, Long> {

    List<RecursoAutocuidado> findAllByOrderByCategoriaAscTituloAsc();

    List<RecursoAutocuidado> findByCategoriaIgnoreCase(String categoria);
}
