
package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Comentario;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.ComentarioRepository;
import com.utp.proyectoriesgo.repository.PublicacionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final PublicacionRepository publicacionRepository;

    public ComentarioService(
            ComentarioRepository comentarioRepository,
            PublicacionRepository publicacionRepository) {

        this.comentarioRepository = comentarioRepository;
        this.publicacionRepository = publicacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarComentarios(Long publicacionId) {

        Publicacion publicacion = publicacionRepository
                .findById(publicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La publicación no existe"));

        if (publicacion.isOculta()) {
            throw new RecursoNoEncontradoException("La publicación no está disponible");
        }

        return comentarioRepository
                .findByPublicacionIdAndOcultoFalseOrderByFechaAsc(
                        publicacionId
                );
    }

    @Transactional
    public Comentario crearComentario(
            Long publicacionId,
            String texto,
            Usuario autor) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "El comentario no puede estar vacío"
            );
        }

        String textoLimpio = texto.trim();

        if (textoLimpio.length() > 500) {
            throw new IllegalArgumentException(
                    "El comentario no puede superar 500 caracteres"
            );
        }

        if (autor == null) {
            throw new IllegalArgumentException(
                    "El comentario necesita una autora"
            );
        }

        Publicacion publicacion = publicacionRepository
                .findById(publicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La publicación no existe"));

        if (publicacion.isOculta()) {
            throw new IllegalArgumentException(
                    "No se puede comentar una publicación oculta"
            );
        }

        Comentario comentario = new Comentario();

        comentario.setPublicacion(publicacion);
        comentario.setAutor(autor);
        comentario.setTexto(textoLimpio);
        comentario.setOculto(false);

        return comentarioRepository.save(comentario);
    }
}
