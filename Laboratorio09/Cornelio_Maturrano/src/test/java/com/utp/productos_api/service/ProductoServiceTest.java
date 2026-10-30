
package com.utp.productosapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.utp.productosapi.model.Producto;
import com.utp.productosapi.repository.ProductoRepository;
import com.utp.productosapi.repository.MovimientoStockRepository;

class ProductoServiceTest {

    private ProductoService service;
    private ProductoRepository repository;
    private MovimientoStockRepository movimientoRepository;

    @BeforeEach
    void setUp() {
        repository = mock(ProductoRepository.class);
        movimientoRepository = mock(MovimientoStockRepository.class);

        service = new ProductoService(
            repository,
            movimientoRepository
        );
    }

    // PRUEBA 1: CREAR PRODUCTO
    @Test
    void debeCrearProductoYAsignarId() {
        Producto nuevo = new Producto(
            "Laptop",
            "Tecnologia",
            new BigDecimal("3500.00"),
            10
        );

        when(repository.save(any(Producto.class)))
            .thenAnswer(invocacion -> {
                Producto producto = invocacion.getArgument(0);
                producto.setId(1L);
                return producto;
            });

        Producto creado = service.crear(nuevo);

        assertNotNull(creado.getId());
        assertEquals("Laptop", creado.getNombre());
        assertEquals(
            0,
            new BigDecimal("3500.00").compareTo(creado.getPrecio())
        );

        verify(repository).save(any(Producto.class));
    }

    // PRUEBA 2: BUSCAR PRODUCTO POR ID
    @Test
    void debeBuscarProductoPorId() {
        Producto producto = new Producto(
            "Mouse",
            "Accesorios",
            new BigDecimal("80.00"),
            20
        );
        producto.setId(1L);

        when(repository.findById(1L))
            .thenReturn(Optional.of(producto));

        Producto encontrado = service.buscarPorId(1L).orElseThrow();

        assertEquals("Mouse", encontrado.getNombre());
        assertEquals(
            0,
            new BigDecimal("80.00").compareTo(encontrado.getPrecio())
        );

        verify(repository).findById(1L);
    }

    // PRUEBA 3: RECHAZAR PRECIO INVALIDO
    @Test
    void debeRechazarPrecioInvalido() {
        Producto invalido = new Producto(
            "Monitor",
            "Tecnologia",
            BigDecimal.ZERO,
            5
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> service.crear(invalido)
        );

        verify(repository, never()).save(any(Producto.class));
    }

    // PRUEBA 4: ELIMINAR PRODUCTO
    @Test
    void debeEliminarProducto() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean eliminado = service.eliminar(1L);

        assertTrue(eliminado);
        verify(repository).deleteById(1L);
    }

    // PRUEBA 5: PRODUCTO NO EXISTE
    @Test
    void debeRetornarFalseSiProductoNoExiste() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean eliminado = service.eliminar(99L);

        assertFalse(eliminado);
        verify(repository, never()).deleteById(anyLong());
    }

    // PRUEBA 6: ACTUALIZAR PRODUCTO
    @Test
    void debeActualizarProducto() {
        Producto existente = new Producto(
            "Laptop",
            "Tecnologia",
            new BigDecimal("2500.00"),
            10
        );
        existente.setId(1L);

        Producto datos = new Producto(
            "Laptop ThinkPad",
            "Tecnologia",
            new BigDecimal("3200.00"),
            15
        );

        when(repository.findById(1L))
            .thenReturn(Optional.of(existente));

        when(repository.save(any(Producto.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

        Producto actualizado =
            service.actualizar(1L, datos).orElseThrow();

        assertEquals("Laptop ThinkPad", actualizado.getNombre());
        assertEquals(
            0,
            new BigDecimal("3200.00").compareTo(actualizado.getPrecio())
        );
        assertEquals(15, actualizado.getStock());

        verify(repository).save(existente);
    }

    // PRUEBA 7: ACTUALIZAR SOLO PRECIO
    @Test
    void debeActualizarSoloPrecio() {
        Producto existente = new Producto(
            "Laptop",
            "Tecnologia",
            new BigDecimal("2500.00"),
            10
        );
        existente.setId(1L);

        when(repository.findById(1L))
            .thenReturn(Optional.of(existente));

        when(repository.save(any(Producto.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

        Producto actualizado = service.actualizarPrecio(
            1L,
            new BigDecimal("2900.00")
        ).orElseThrow();

        assertEquals(
            0,
            new BigDecimal("2900.00").compareTo(actualizado.getPrecio())
        );
        assertEquals("Laptop", actualizado.getNombre());
        assertEquals(10, actualizado.getStock());

        verify(repository).save(existente);
    }
}
