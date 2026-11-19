
package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.ComentarioRequest;
import com.utp.proyectoriesgo.dto.ComentarioResponse;
import com.utp.proyectoriesgo.model.Comentario;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.ComentarioService;
import com.utp.proyectoriesgo.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunidad/publicaciones/{publicacionId}/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final UsuarioService usuarioService;

    public ComentarioController(
            ComentarioService comentarioService,
            UsuarioService usuarioService) {

        this.comentarioService = comentarioService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<ComentarioResponse> listar(
            @PathVariable Long publicacionId) {

        return comentarioService
                .listarComentarios(publicacionId)
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ComentarioResponse> crear(
            @PathVariable Long publicacionId,
            @Valid @RequestBody ComentarioRequest request,
            Authentication autenticacion) {

        Usuario autor = usuarioService.actual(autenticacion);

        Comentario comentario = comentarioService.crearComentario(
                publicacionId,
                request.texto(),
                autor
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirRespuesta(comentario));
    }

    private ComentarioResponse convertirRespuesta(
            Comentario comentario) {

        String seudonimo = comentario.getAutor().getSeudonimo();

        if (seudonimo == null || seudonimo.isBlank()) {
            seudonimo = "Anónima";
        }

        return new ComentarioResponse(
                comentario.getId(),
                comentario.getPublicacion().getId(),
                seudonimo,
                comentario.getTexto(),
                comentario.getFecha()
        );
    }
}
