package com.utp.proyectoriesgo.service;

/**
 * Punto de extensión para "Eliminar mis datos" (módulo Mi seguridad / privacidad).
 *
 * CADA módulo que guarde datos de una usuaria debe tener un @Component que implemente esta
 * interfaz y borre TODO lo suyo (evaluaciones, plan, contactos, diario, publicaciones...).
 * El servicio de cuenta recibe la lista completa con inyección:
 *
 *     List<LimpiezaDatosUsuaria> limpiezas;   // Spring inyecta todas las implementaciones
 *     limpiezas.forEach(l -> l.eliminarDatosDe(usuarioId));
 *
 * Así nadie tiene que editar el código de otro módulo.
 */
public interface LimpiezaDatosUsuaria {

    /** Borra todos los datos de la usuaria en este módulo. Debe ser idempotente. */
    void eliminarDatosDe(Long usuarioId);
}
