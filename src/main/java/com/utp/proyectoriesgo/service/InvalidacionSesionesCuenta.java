package com.utp.proyectoriesgo.service;

/**
 * Integración con la autoridad que valida sesiones. La implementación debe participar en
 * la transacción del borrado y dejar inutilizables todos los tokens de la identidad eliminada,
 * incluso si se vuelve a registrar el mismo nombre de usuario.
 */
public interface InvalidacionSesionesCuenta {
    void invalidarSesionesDe(Long usuarioId);
}
