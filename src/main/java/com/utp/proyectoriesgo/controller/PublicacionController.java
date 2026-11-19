
package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.PublicacionRequest;
import com.utp.proyectoriesgo.dto.PublicacionResponse;
import com.utp.proyectoriesgo.dto.ReaccionesResponse;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.ComentarioService;
import com.utp.proyectoriesgo.service.PublicacionService;
import com.utp.proyectoriesgo.service.ReaccionService;
import com.utp.proyectoriesgo.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comunidad/publicaciones")
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final UsuarioService usuarioService;
    private final ReaccionService reaccionService;
    private final ComentarioService comentarioService;

    public PublicacionController(
            PublicacionService publicacionService,
            UsuarioService usuarioService,
            ReaccionService reaccionService,
            ComentarioService comentarioService) {

        this.publicacionService = publicacionService;
        this.usuarioService = usuarioService;
        this.reaccionService = reaccionService;
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public List<PublicacionResponse> listar(Authentication autenticacion) {
        Long usuariaId = usuarioService.actual(autenticacion).getId();

        List<Publicacion> lista = publicacionService.listarPublicaciones();
        List<Long> ids = lista.stream().map(Publicacion::getId).toList();

        // Dos consultas para todo el muro (reacciones y comentarios), no una por publicación.
        Map<Long, ReaccionesResponse> reacciones = reaccionService.resumenes(usuariaId, ids);
        Map<Long, Long> comentarios = comentarioService.contarVisibles(ids);

        return lista.stream()
                .map(publicacion -> convertirRespuesta(
                        publicacion,
                        usuariaId,
                        reacciones.getOrDefault(publicacion.getId(), ReaccionesResponse.vacia()),
                        comentarios.getOrDefault(publicacion.getId(), 0L)))
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
                .body(convertirRespuesta(publicacion, autor.getId(), ReaccionesResponse.vacia(), 0L));
    }

    private PublicacionResponse convertirRespuesta(
            Publicacion publicacion,
            Long usuariaId,
            ReaccionesResponse reacciones,
            long totalComentarios) {

        String seudonimo =
                publicacion.getAutor().getSeudonimo();

        if (seudonimo == null || seudonimo.isBlank()) {
            seudonimo = "Anónima";
        }

        return new PublicacionResponse(
                publicacion.getId(),
                seudonimo,
                publicacion.getTexto(),
                publicacion.getFecha(),
                publicacion.getAutor().getId().equals(usuariaId),
                totalComentarios,
                reacciones
        );
    }
}
