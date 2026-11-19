
package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.PublicacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;

    public PublicacionService(
            PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Publicacion> listarPublicaciones() {
        return publicacionRepository
                .findByOcultaFalseOrderByFechaDesc();
    }

    @Transactional
    public Publicacion crearPublicacion(
            String texto, Usuario autor) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "La publicación no puede estar vacía");
        }

        String textoLimpio = texto.trim();

        if (textoLimpio.length() > 500) {
            throw new IllegalArgumentException(
                    "La publicación no puede superar 500 caracteres");
        }

        if (autor == null) {
            throw new IllegalArgumentException(
                    "La publicación necesita un usuario");
        }

        Publicacion publicacion = new Publicacion();
        publicacion.setTexto(textoLimpio);
        publicacion.setAutor(autor);
        publicacion.setOculta(false);

        return publicacionRepository.save(publicacion);
    }


    // Ocultar una publicación desde moderación
    @Transactional
    public void ocultarPublicacion(Long publicacionId) {

        Publicacion publicacion = publicacionRepository
                .findById(publicacionId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("La publicación no existe"));

        if (publicacion.isOculta()) {
            throw new IllegalArgumentException(
                    "La publicación ya está oculta");
        }

        publicacion.setOculta(true);

        publicacionRepository.save(publicacion);
    }

}
