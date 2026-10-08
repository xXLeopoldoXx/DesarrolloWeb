package com.utp.semana4_api_rest.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.semana4_api_rest.dto.ActualizarStockRequest;
import com.utp.semana4_api_rest.dto.ProductoRequest;
import com.utp.semana4_api_rest.exception.ProductoNoEncontradoException;
import com.utp.semana4_api_rest.model.MovimientoStock;
import com.utp.semana4_api_rest.model.Producto;
import com.utp.semana4_api_rest.repository.MovimientoStockRepository;
import com.utp.semana4_api_rest.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final MovimientoStockRepository movimientoRepository;

    public ProductoService(
            ProductoRepository repository,
            MovimientoStockRepository movimientoRepository) {

        this.repository = repository;
        this.movimientoRepository = movimientoRepository;
    }

    // LISTAR PRODUCTOS
    @Transactional(readOnly = true)
    public List<Producto> listar(String categoria) {

        if (categoria != null) {
            return repository.findByCategoriaIgnoreCase(categoria);
        }

        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    // BUSCAR PRODUCTO POR ID
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ProductoNoEncontradoException(id));
    }

    // CREAR PRODUCTO CON DTO
    @Transactional
    public Producto crear(ProductoRequest request) {

        Producto producto = new Producto(
                null,
                request.getNombre(),
                request.getCategoria(),
                request.getPrecio(),
                request.getStock());

        validar(producto);

        return repository.save(producto);
    }

    // CREAR PRODUCTO
    @Transactional
    public Producto crear(Producto producto) {

        validar(producto);
        producto.setId(null);

        return repository.save(producto);
    }

    // ACTUALIZAR PRODUCTO
    @Transactional
    public Producto actualizar(Long id, ProductoRequest request) {

        Producto producto = buscarPorId(id);

        producto.setNombre(request.getNombre());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());

        validar(producto);

        return repository.save(producto);
    }

    // ACTUALIZAR STOCK
    @Transactional
    public Producto actualizarStock(
            Long id,
            ActualizarStockRequest request) {

        Producto producto = buscarPorId(id);

        producto.setStock(request.getStock());

        validar(producto);

        return repository.save(producto);
    }

    // ACTUALIZAR PRECIO
    @Transactional
    public Producto actualizarPrecio(Long id, double precio) {

        if (precio <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero");
        }

        Producto producto = buscarPorId(id);
        producto.setPrecio(precio);

        return repository.save(producto);
    }

    // JPQL: BUSCAR POR NOMBRE
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "El texto de búsqueda es obligatorio");
        }

        return repository.buscarPorNombre(texto.trim());
    }

    // CONSULTA DERIVADA: BUSCAR POR CATEGORIA
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String categoria) {

        return repository.findByCategoriaIgnoreCase(categoria);
    }

    // JPQL: BUSCAR POR RANGO DE PRECIO
    @Transactional(readOnly = true)
    public List<Producto> buscarPorRango(double min, double max) {

        if (min > max) {
            throw new IllegalArgumentException(
                    "El precio mínimo no puede superar al máximo");
        }

        return repository.buscarPorRangoPrecio(min, max);
    }

    // JPQL: BUSCAR PRODUCTOS CON STOCK BAJO
    @Transactional(readOnly = true)
    public List<Producto> buscarConStockBajo(Integer limite) {

        if (limite == null || limite < 0) {
            throw new IllegalArgumentException(
                    "El límite no puede ser negativo");
        }

        return repository.buscarConStockBajo(limite);
    }

    // TRANSACCION: REGISTRAR SALIDA DE INVENTARIO
    @Transactional
    public Producto registrarSalida(Long id, int cantidad) {

        Producto producto = buscarPorId(id);
        validarMovimiento(producto, cantidad, false);

        producto.setStock(producto.getStock() - cantidad);

        MovimientoStock movimiento = new MovimientoStock(
                producto,
                "SALIDA",
                cantidad,
                LocalDateTime.now());

        movimientoRepository.save(movimiento);

        return producto;
    }

    // TRANSACCION: SIMULAR ERROR PARA ROLLBACK
    @Transactional
    public void simularSalidaConError(Long id, int cantidad) {

        Producto producto = buscarPorId(id);
        validarMovimiento(producto, cantidad, false);

        producto.setStock(producto.getStock() - cantidad);

        MovimientoStock movimiento = new MovimientoStock(
                producto,
                "SALIDA",
                cantidad,
                LocalDateTime.now());

        movimientoRepository.save(movimiento);

        throw new IllegalStateException(
                "Error simulado: la transacción debe hacer rollback");
    }

    // TRANSACCION: REGISTRAR ENTRADA DE INVENTARIO
    @Transactional
    public Producto registrarEntrada(Long id, int cantidad) {

        Producto producto = buscarPorId(id);
        validarMovimiento(producto, cantidad, true);

        producto.setStock(producto.getStock() + cantidad);

        MovimientoStock movimiento = new MovimientoStock(
                producto,
                "ENTRADA",
                cantidad,
                LocalDateTime.now());

        movimientoRepository.save(movimiento);

        return producto;
    }

    // ELIMINAR PRODUCTO
    @Transactional
    public void eliminar(Long id) {

        if (!repository.existsById(id)) {
            throw new ProductoNoEncontradoException(id);
        }

        repository.deleteById(id);
    }

    // VALIDAR MOVIMIENTOS DE INVENTARIO
    private void validarMovimiento(
            Producto producto,
            int cantidad,
            boolean entrada) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero");
        }

        if (!entrada && producto.getStock() < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Disponible: "
                            + producto.getStock());
        }

        if (entrada && cantidad > Integer.MAX_VALUE - producto.getStock()) {
            throw new IllegalArgumentException(
                    "La cantidad supera el stock máximo permitido");
        }
    }

    // VALIDACIONES DE PRODUCTO
    private void validar(Producto producto) {

        if (producto.getNombre() == null
                || producto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero");
        }

        if (producto.getStock() < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo");
        }
    }
}
