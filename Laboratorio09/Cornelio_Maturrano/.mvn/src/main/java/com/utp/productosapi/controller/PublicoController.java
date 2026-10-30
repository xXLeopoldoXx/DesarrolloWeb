package com.utp.productosapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/publico")
public class PublicoController {

    @GetMapping
    public String mensajePublico() {
        return "Bienvenido a la API de Productos. Acceso publico.";
    }
}
