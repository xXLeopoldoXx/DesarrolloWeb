package com.utp.proyectoriesgo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Clave SOLO de prueba (32 bytes en Base64) para que el test no dependa de la variable de entorno.
@SpringBootTest(properties = "app.jwt.secret-base64=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=")
class ProyectoRiesgoApplicationTests {

    @Test
    void contextLoads() {
    }
}
