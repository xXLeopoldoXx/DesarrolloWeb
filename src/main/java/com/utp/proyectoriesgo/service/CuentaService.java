package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.BorradoCuentaNoDisponibleException;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class CuentaService {
    private static final Set<String> LIMPIEZAS_REQUERIDAS = Set.of(
            "planSeguridadService", "contactoService", "autoevaluacionService", "diarioService", "comunidadService");

    private final UsuarioRepository usuarias;
    private final List<LimpiezaDatosUsuaria> limpiezas;
    private final Optional<InvalidacionSesionesCuenta> invalidacion;
    private final boolean habilitado;

    public CuentaService(UsuarioRepository usuarias, Map<String, LimpiezaDatosUsuaria> limpiezas,
                         Optional<InvalidacionSesionesCuenta> invalidacion,
                         @Value("${app.seguridad.borrado-cuenta.habilitado:false}") boolean autorizado) {
        this.usuarias = usuarias;
        this.limpiezas = List.copyOf(limpiezas.values());
        this.invalidacion = invalidacion;
        this.habilitado = autorizado && limpiezas.keySet().containsAll(LIMPIEZAS_REQUERIDAS);
    }

    public boolean borradoDisponible() {
        return habilitado && invalidacion.isPresent();
    }

    @Transactional
    public void eliminarCuenta(Usuario usuaria) {
        if (!habilitado) {
            throw new BorradoCuentaNoDisponibleException();
        }
        InvalidacionSesionesCuenta sesiones = invalidacion.orElseThrow(BorradoCuentaNoDisponibleException::new);
        Long id = usuaria.getId();
        Usuario cuenta = usuarias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuaria no encontrada."));
        for (LimpiezaDatosUsuaria limpieza : limpiezas) {
            limpieza.eliminarDatosDe(id);
        }
        sesiones.invalidarSesionesDe(id);
        usuarias.delete(cuenta);
    }
}
