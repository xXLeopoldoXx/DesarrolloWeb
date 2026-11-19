package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.ContactoRequest;
import com.utp.proyectoriesgo.dto.ContactoResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.ContactoConfianza;
import com.utp.proyectoriesgo.repository.ContactoConfianzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContactoService implements LimpiezaDatosUsuaria {
    private final ContactoConfianzaRepository contactos;

    public ContactoService(ContactoConfianzaRepository contactos) {
        this.contactos = contactos;
    }

    public List<ContactoResponse> listar(Long usuarioId) {
        return contactos.findByUsuarioIdOrderByIdAsc(usuarioId).stream()
                .map(ContactoService::respuesta)
                .toList();
    }

    @Transactional
    public ContactoResponse crear(Long usuarioId, ContactoRequest solicitud) {
        ContactoConfianza contacto = new ContactoConfianza(usuarioId, solicitud.nombre().strip(),
                solicitud.telefono(), relacion(solicitud));
        return respuesta(contactos.save(contacto));
    }

    @Transactional
    public ContactoResponse actualizar(Long usuarioId, Long id, ContactoRequest solicitud) {
        ContactoConfianza contacto = buscar(usuarioId, id);
        contacto.actualizar(solicitud.nombre().strip(), solicitud.telefono(), relacion(solicitud));
        return respuesta(contacto);
    }

    @Transactional
    public void eliminar(Long usuarioId, Long id) {
        contactos.delete(buscar(usuarioId, id));
    }

    @Override
    @Transactional
    public void eliminarDatosDe(Long usuarioId) {
        contactos.deleteByUsuarioId(usuarioId);
    }

    private ContactoConfianza buscar(Long usuarioId, Long id) {
        return contactos.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contacto no encontrado."));
    }

    private static ContactoResponse respuesta(ContactoConfianza contacto) {
        return new ContactoResponse(contacto.getId(), contacto.getNombre(), contacto.getTelefono(),
                contacto.getRelacion());
    }

    private static String relacion(ContactoRequest solicitud) {
        String valor = solicitud.relacion();
        return valor == null || valor.isBlank() ? null : valor.strip();
    }
}
