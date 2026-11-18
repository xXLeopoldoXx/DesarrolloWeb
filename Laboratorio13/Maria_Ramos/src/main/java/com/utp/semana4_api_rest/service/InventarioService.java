package com.utp.semana4_api_rest.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.semana4_api_rest.model.MovimientoStock;
import com.utp.semana4_api_rest.model.Producto;
import com.utp.semana4_api_rest.repository.MovimientoStockRepository;
import com.utp.semana4_api_rest.repository.ProductoRepository;

@Service
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;

    public InventarioService(
            ProductoRepository productoRepository,
            MovimientoStockRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    public Producto registrarSalida(Long productoId, int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el producto con ID: " + productoId));

        if (producto.getStock() < cantidad) {
            throw new IllegalStateException(
                    "Stock insuficiente. Stock actual: "
                    + producto.getStock()
                    + ", cantidad solicitada: " + cantidad);
        }

        producto.setStock(producto.getStock() - cantidad);

        MovimientoStock movimiento = new MovimientoStock(
                producto,
                "SALIDA",
                cantidad,
                LocalDateTime.now()
        );

        movimientoRepository.save(movimiento);

        return producto;
    }
}
