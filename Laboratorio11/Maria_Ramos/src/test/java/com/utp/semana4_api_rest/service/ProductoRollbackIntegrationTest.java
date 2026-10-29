package com.utp.semana4_api_rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.utp.semana4_api_rest.model.Producto;
import com.utp.semana4_api_rest.repository.MovimientoStockRepository;
import com.utp.semana4_api_rest.repository.ProductoRepository;

@SpringBootTest
@ActiveProfiles("test")
class ProductoRollbackIntegrationTest {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoStockRepository movimientoStockRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Long productoId;

    @BeforeEach
    void prepararDatos() {
        // Limpiar los movimientos antes de eliminar los productos.
        movimientoStockRepository.deleteAll();
        productoRepository.deleteAll();

        Producto producto = new Producto(
                null,
                "Producto para rollback",
                "Pruebas",
                100.0,
                10
        );

        productoId = productoRepository.saveAndFlush(producto).getId();
    }

    @Test
    void debeRevertirStockYMovimientoCuandoOcurreUnError() {
        long movimientosAntes = movimientoStockRepository.count();

        // La excepción debe provocar que Spring revierta la transacción.
        assertThrows(
                IllegalStateException.class,
                () -> productoService.simularSalidaConError(productoId, 3)
        );

        // Consultar los datos en una transacción nueva.
        TransactionTemplate transaccionNueva =
                new TransactionTemplate(transactionManager);

        transaccionNueva.executeWithoutResult(status -> {
            Producto productoActualizado = productoRepository.findById(productoId)
                    .orElseThrow();

            assertEquals(10, productoActualizado.getStock(),
                    "El stock debe conservar su valor original");

            assertEquals(movimientosAntes, movimientoStockRepository.count(),
                    "No debe quedar guardado el movimiento de salida");
        });
    }
}
