package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.AccionRequest;
import com.utp.proyectoriesgo.dto.AccionResponse;
import com.utp.proyectoriesgo.dto.LugarRequest;
import com.utp.proyectoriesgo.dto.LugarResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.LugarSeguro;
import com.utp.proyectoriesgo.model.PlanAccion;
import com.utp.proyectoriesgo.repository.LugarSeguroRepository;
import com.utp.proyectoriesgo.repository.PlanAccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PlanSeguridadService implements LimpiezaDatosUsuaria {
    private final PlanAccionRepository acciones;
    private final LugarSeguroRepository lugares;

    public PlanSeguridadService(PlanAccionRepository acciones, LugarSeguroRepository lugares) {
        this.acciones = acciones;
        this.lugares = lugares;
    }

    public List<AccionResponse> listarAcciones(Long usuarioId) {
        return acciones.findByUsuarioIdOrderByIdAsc(usuarioId).stream()
                .map(PlanSeguridadService::respuestaAccion)
                .toList();
    }

    @Transactional
    public AccionResponse crearAccion(Long usuarioId, AccionRequest solicitud) {
        return respuestaAccion(acciones.save(new PlanAccion(usuarioId, solicitud.texto().strip())));
    }

    @Transactional
    public AccionResponse marcarAccion(Long usuarioId, Long id, boolean completada) {
        PlanAccion accion = buscarAccion(usuarioId, id);
        accion.marcar(completada);
        return respuestaAccion(accion);
    }

    @Transactional
    public void eliminarAccion(Long usuarioId, Long id) {
        acciones.delete(buscarAccion(usuarioId, id));
    }

    public List<LugarResponse> listarLugares(Long usuarioId) {
        return lugares.findByUsuarioIdOrderByIdAsc(usuarioId).stream()
                .map(PlanSeguridadService::respuestaLugar)
                .toList();
    }

    @Transactional
    public LugarResponse crearLugar(Long usuarioId, LugarRequest solicitud) {
        LugarSeguro lugar = new LugarSeguro(usuarioId, solicitud.nombre().strip(),
                limpiarOpcional(solicitud.referencia()), limpiarOpcional(solicitud.nota()));
        return respuestaLugar(lugares.save(lugar));
    }

    @Transactional
    public void eliminarLugar(Long usuarioId, Long id) {
        LugarSeguro lugar = lugares.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lugar seguro no encontrado."));
        lugares.delete(lugar);
    }

    @Override
    @Transactional
    public void eliminarDatosDe(Long usuarioId) {
        acciones.deleteByUsuarioId(usuarioId);
        lugares.deleteByUsuarioId(usuarioId);
    }

    private PlanAccion buscarAccion(Long usuarioId, Long id) {
        return acciones.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acción no encontrada."));
    }

    private static AccionResponse respuestaAccion(PlanAccion accion) {
        return new AccionResponse(accion.getId(), accion.getTexto(), accion.isCompletada());
    }

    private static LugarResponse respuestaLugar(LugarSeguro lugar) {
        return new LugarResponse(lugar.getId(), lugar.getNombre(), lugar.getReferencia(), lugar.getNota());
    }

    private static String limpiarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.strip();
    }
}
