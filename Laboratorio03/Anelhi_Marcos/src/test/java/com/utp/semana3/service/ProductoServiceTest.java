package com.utp.semana3.service;

import com.utp.semana3.model.Producto;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ProductoServiceTest {

    @Test
    void registrarProductoValido_debeAsignarIdYGuardar() {
        ProductoService service = new ProductoService();
        Producto producto = new Producto(null, "Laptop", 3500.00, 10);

        Producto registrado = service.registrar(producto);

        assertThat(registrado.getId()).isNotNull();
        assertThat(registrado.getNombre()).isEqualTo("Laptop");
        assertThat(service.listar()).hasSize(1);
    }
}
