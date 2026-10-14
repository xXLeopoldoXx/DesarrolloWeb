package com.utp.semana4.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.utp.semana4.model.Producto;
import com.utp.semana4.exception.ProductoNoEncontradoException;

class ProductoServiceTest {

    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService();
    }

    @Test
    void debeCrearProductoYAsignarId() {
        Producto nuevo = new Producto(
                null,
                "Laptop",
                "Tecnologia",
                3500.0,
                10
        );

        Producto creado = service.crear(nuevo);

        assertNotNull(creado.getId());
        assertEquals("Laptop", creado.getNombre());
        assertEquals(3500.0, creado.getPrecio());
    }

    @Test
void debeBuscarProductoPorId() {
    Producto creado = service.crear(
            new Producto(null, "Mouse", "Tecnologia", 80.0, 20));

    Producto encontrado = service.buscarPorId(creado.getId());

    assertEquals("Mouse", encontrado.getNombre());
}

@Test
void debeRechazarPrecioInvalido() {
    Producto invalido = new Producto(
            null, "Monitor", "Tecnologia", 0.0, 5);

    assertThrows(
            IllegalArgumentException.class,
            () -> service.crear(invalido));
}

@Test
void debeEliminarProducto() {
    Producto creado = service.crear(
            new Producto(null, "Teclado", "Tecnologia", 120.0, 8));

    service.eliminar(creado.getId());

    assertThrows(
            ProductoNoEncontradoException.class,
            () -> service.buscarPorId(creado.getId()));
}
}