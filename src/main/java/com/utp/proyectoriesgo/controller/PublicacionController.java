
package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.PublicacionRequest;
import com.utp.proyectoriesgo.dto.PublicacionResponse;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.PublicacionService;
import com.utp.proyectoriesgo.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunidad/publicaciones")
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final UsuarioService usuarioService;

    public PublicacionController(
            PublicacionService publicacionService,
            UsuarioService usuarioService) {

        this.publicacionService = publicacionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<PublicacionResponse> listar() {
        return publicacionService.listarPublicaciones()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @PostMapping
    public ResponseEntity<PublicacionResponse> crear(
            @Valid @RequestBody PublicacionRequest request,
            Authentication autenticacion) {

        Usuario autor = usuarioService.actual(autenticacion);

        Publicacion publicacion =
                publicacionService.crearPublicacion(
                        request.texto(), autor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirRespuesta(publicacion));
    }

    private PublicacionResponse convertirRespuesta(
            Publicacion publicacion) {

        String seudonimo =
                publicacion.getAutor().getSeudonimo();

        if (seudonimo == null || seudonimo.isBlank()) {
            seudonimo = "Anónima";
        }

        return new PublicacionResponse(
                publicacion.getId(),
                seudonimo,
                publicacion.getTexto(),
                publicacion.getFecha()
        );
    }
}
