package com.utp.productosapi.service;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.utp.productosapi.model.Producto;

class ProductoServiceTest {
    private ProductoService service;

    @BeforeEach
    void setup() {
        service = new ProductoService();
    }

    @Test
    void debeCrearProductoYAsignarId() {
        Producto nuevo = new Producto(null, "Laptop", 3500.0, 10, "Electrónica");
        Producto creado = service.crear(nuevo);
        
        assertNotNull(creado.getId());
        assertEquals("Laptop", creado.getNombre());
        assertEquals(3500.0, creado.getPrecio());
    }

    @Test
    void debeBuscarProductoPorId() {
        Producto creado = service.crear(new Producto(null, "Mouse", 80.0, 20, "Periféricos"));
        Producto encontrado = service.buscarPorId(creado.getId()).orElseThrow();
        assertEquals("Mouse", encontrado.getNombre());
    }

    @Test
    void debeRechazarPrecioInvalido() {
        Producto invalido = new Producto(null, "Monitor", 0.0, 5, "Electrónica");
        assertThrows(IllegalArgumentException.class, () -> service.crear(invalido));
    }

    @Test
    void debeEliminarProducto() {
        Producto creado = service.crear(new Producto(null, "Teclado", 120.0, 8, "Periféricos"));
        boolean eliminado = service.eliminar(creado.getId());
        assertTrue(eliminado);
        assertTrue(service.buscarPorId(creado.getId()).isEmpty());
    }

    // Prueba del Reto Integrador
    @Test
    void debeBuscarProductosPorNombre() {
        service.crear(new Producto(null, "Laptop Gamer", 4500.0, 5, "Electrónica"));
        service.crear(new Producto(null, "Teclado Mecánico", 250.0, 15, "Periféricos"));
        service.crear(new Producto(null, "Funda para laptop", 50.0, 30, "Accesorios"));

        List<Producto> resultados = service.buscarPorNombre("lap");
        
        assertEquals(2, resultados.size());
    }
}