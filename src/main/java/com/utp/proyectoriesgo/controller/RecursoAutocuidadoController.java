package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.RecursoAutocuidadoResponse;
import com.utp.proyectoriesgo.service.RecursoAutocuidadoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/mi/bienestar/recursos", "/api/mi/recursos-autocuidado"})
public class RecursoAutocuidadoController {

    private final RecursoAutocuidadoService recursoAutocuidadoService;

    public RecursoAutocuidadoController(RecursoAutocuidadoService recursoAutocuidadoService) {
        this.recursoAutocuidadoService = recursoAutocuidadoService;
    }

    /**
     * Lista los recursos de autocuidado disponibles.
     * Permite filtrar opcionalmente por categoría (ej. MINDFULNESS, EMOCIONAL, FISICO, SOCIAL, SEGURIDAD).
     */
    @GetMapping
    public List<RecursoAutocuidadoResponse> listar(
            @RequestParam(name = "categoria", required = false) String categoria) {
        return recursoAutocuidadoService.listar(categoria);
    }
}
