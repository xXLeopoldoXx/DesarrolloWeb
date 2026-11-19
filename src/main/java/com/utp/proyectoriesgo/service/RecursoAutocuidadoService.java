package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.RecursoAutocuidadoResponse;
import com.utp.proyectoriesgo.model.RecursoAutocuidado;
import com.utp.proyectoriesgo.repository.RecursoAutocuidadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecursoAutocuidadoService {

    private final RecursoAutocuidadoRepository repository;

    public RecursoAutocuidadoService(RecursoAutocuidadoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RecursoAutocuidadoResponse> listar(String categoria) {
        List<RecursoAutocuidado> recursos;
        if (categoria != null && !categoria.isBlank()) {
            recursos = repository.findByCategoriaIgnoreCase(categoria.trim());
        } else {
            recursos = repository.findAllByOrderByCategoriaAscTituloAsc();
        }

        return recursos.stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private RecursoAutocuidadoResponse mapearAResponse(RecursoAutocuidado r) {
        return new RecursoAutocuidadoResponse(
                r.getId(),
                r.getCategoria(),
                r.getTitulo(),
                r.getDescripcion(),
                r.getTipo(),
                r.getEnlaceOConsejo()
        );
    }
}
