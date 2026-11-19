package com.utp.tienda.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.tienda.exception.RecursoNoEncontradoException;
import com.utp.tienda.exception.ReglaNegocioException;
import com.utp.tienda.model.MovimientoStock;
import com.utp.tienda.model.Producto;
import com.utp.tienda.repository.MovimientoStockRepository;
import com.utp.tienda.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;

    public ProductoService(ProductoRepository productoRepository,
                           MovimientoStockRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String texto) {
        return productoRepository.buscarPorNombre(texto);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorRango(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new ReglaNegocioException("El precio mínimo no puede superar al máximo");
        }
        return productoRepository.buscarPorRangoPrecio(min, max);
    }

    @Transactional(readOnly = true)
    public long contarTotal() {
        return productoRepository.count();
    }

    @Transactional
    public Producto crear(Producto producto) {
        validar(producto);
        producto.setId(null);
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(Long id, Producto datos) {
        validar(datos);
        Producto existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setCategoria(datos.getCategoria());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        return productoRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto no encontrado: " + id);
        }
        productoRepository.deleteById(id);
    }

    @Transactional
    public Producto registrarSalida(Long productoId, int cantidad) {
        Producto producto = buscarPorId(productoId);
        validarSalida(producto, cantidad);

        producto.setStock(producto.getStock() - cantidad);
        MovimientoStock movimiento = new MovimientoStock(
                producto, "SALIDA", cantidad, LocalDateTime.now());
        movimientoRepository.save(movimiento);

        return producto;
    }

    private void validar(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre es obligatorio");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El precio debe ser mayor que cero");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new ReglaNegocioException("El stock no puede ser negativo");
        }
    }

    private void validarSalida(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
        if (producto.getStock() < cantidad) {
            throw new ReglaNegocioException("Stock insuficiente. Disponible: " + producto.getStock());
        }
    }
}
