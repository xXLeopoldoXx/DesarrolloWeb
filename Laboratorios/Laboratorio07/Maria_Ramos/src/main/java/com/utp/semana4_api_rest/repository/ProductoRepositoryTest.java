package com.utp.semana4_api_rest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.utp.semana4_api_rest.model.Producto;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    @Test
    void deberiaGuardarYBuscarProducto() {

        Producto producto = new Producto(
                null,
                "Teclado Mecanico",
                "Tecnologia",
                250.0,
                10
        );

        Producto guardado = repository.save(producto);

        assertThat(guardado.getId()).isNotNull();

        Producto encontrado = repository.findById(guardado.getId())
                .orElseThrow();

        assertThat(encontrado.getNombre())
                .isEqualTo("Teclado Mecanico");

        assertThat(encontrado.getCategoria())
                .isEqualTo("Tecnologia");

        assertThat(encontrado.getPrecio())
                .isEqualTo(250.0);

        assertThat(encontrado.getStock())
                .isEqualTo(10);
    }
}