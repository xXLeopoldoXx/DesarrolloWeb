package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.BorradoCuentaNoDisponibleException;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CuentaService {
    private final UsuarioRepository usuarias;
    private final List<LimpiezaDatosUsuaria> limpiezas;
    private final DisponibilidadBorradoCuenta disponibilidad;

    public CuentaService(UsuarioRepository usuarias, List<LimpiezaDatosUsuaria> limpiezas,
                         DisponibilidadBorradoCuenta disponibilidad) {
        this.usuarias = usuarias;
        this.limpiezas = List.copyOf(limpiezas);
        this.disponibilidad = disponibilidad;
    }

    public boolean borradoDisponible() {
        return disponibilidad.disponible();
    }

    @Transactional
    public void eliminarCuenta(Usuario usuaria) {
        if (!borradoDisponible()) {
            throw new BorradoCuentaNoDisponibleException();
        }
        Long id = usuaria.getId();
        Usuario cuenta = usuarias.buscarParaBorrado(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuaria no encontrada."));
        try {
            for (LimpiezaDatosUsuaria limpieza : limpiezas) {
                limpieza.eliminarDatosDe(id);
            }
            // El validador JWT rechaza esta identidad al confirmarse la transacción.
            usuarias.delete(cuenta);
            usuarias.flush();
        } catch (RuntimeException error) {
            throw new IllegalStateException("No se pudo eliminar la cuenta. Inténtalo de nuevo más tarde.", error);
        }
    }
}
