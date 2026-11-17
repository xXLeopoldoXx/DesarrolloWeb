package com.utp.semana4_api_rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.utp.semana4_api_rest.exception.ProductoNoEncontradoException;
import com.utp.semana4_api_rest.exception.StockInsuficienteException;
import com.utp.semana4_api_rest.model.Producto;
import com.utp.semana4_api_rest.repository.MovimientoStockRepository;
import com.utp.semana4_api_rest.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private MovimientoStockRepository movimientoRepository;

    @InjectMocks
    private ProductoService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto(
                1L, "Laptop", "Tecnologia", 3500.0, 10);
    }

    @Test
    void debeBuscarProductoPorId() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(producto));

        Producto encontrado = service.buscarPorId(1L);

        assertEquals("Laptop", encontrado.getNombre());
    }

    @Test
    void debeLanzarExcepcionCuandoProductoNoExiste() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductoNoEncontradoException.class,
                () -> service.buscarPorId(99L));
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
    void debeRegistrarSalidaYGuardarMovimiento() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(producto));

        Producto resultado = service.registrarSalida(1L, 3);

        assertEquals(7, resultado.getStock());
        verify(movimientoRepository).save(any());
    }

    @Test
    void debeRechazarSalidaConStockInsuficiente() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(producto));

        assertThrows(
                StockInsuficienteException.class,
                () -> service.registrarSalida(1L, 15));

        assertEquals(10, producto.getStock());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void debeLanzarErrorAlSimularSalidaParaRollback() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(producto));

        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> service.simularSalidaConError(1L, 3));

        assertEquals(
                "Error simulado: la transacción debe hacer rollback",
                excepcion.getMessage());

        assertEquals(7, producto.getStock());
        verify(movimientoRepository).save(any());
    }
}
