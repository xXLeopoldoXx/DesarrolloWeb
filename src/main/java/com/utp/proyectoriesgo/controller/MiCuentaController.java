package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.ErrorResponse;
import com.utp.proyectoriesgo.dto.EstadoBorradoCuentaResponse;
import com.utp.proyectoriesgo.exception.BorradoCuentaNoDisponibleException;
import com.utp.proyectoriesgo.service.CuentaService;
import com.utp.proyectoriesgo.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/mi/cuenta")
public class MiCuentaController {
    private final CuentaService cuentas;
    private final UsuarioService usuarias;

    public MiCuentaController(CuentaService cuentas, UsuarioService usuarias) {
        this.cuentas = cuentas;
        this.usuarias = usuarias;
    }

    @GetMapping("/estado")
    public EstadoBorradoCuentaResponse estado(Authentication autenticacion) {
        usuarias.actual(autenticacion);
        return new EstadoBorradoCuentaResponse(cuentas.borradoDisponible());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCuenta(Authentication autenticacion) {
        cuentas.eliminarCuenta(usuarias.actual(autenticacion));
    }

    @ExceptionHandler(BorradoCuentaNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> borradoNoDisponible(BorradoCuentaNoDisponibleException error,
                                                            HttpServletRequest solicitud) {
        HttpStatus estado = HttpStatus.SERVICE_UNAVAILABLE;
        var respuesta = new ErrorResponse(LocalDateTime.now(), estado.value(), estado.getReasonPhrase(),
                error.getMessage(), solicitud.getRequestURI());
        return ResponseEntity.status(estado).body(respuesta);
    }
}
