package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.ContactoConfianza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactoConfianzaRepository extends JpaRepository<ContactoConfianza, Long> {
    List<ContactoConfianza> findByUsuarioIdOrderByIdAsc(Long usuarioId);

    Optional<ContactoConfianza> findByIdAndUsuarioId(Long id, Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
