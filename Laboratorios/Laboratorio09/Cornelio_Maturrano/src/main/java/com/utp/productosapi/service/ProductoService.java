package com.utp.productosapi.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.productosapi.model.Producto;
import com.utp.productosapi.repository.ProductoRepository;

import java.time.LocalDateTime;

import com.utp.productosapi.exception.RecursoNoEncontradoException;
import com.utp.productosapi.exception.ReglaNegocioException;
import com.utp.productosapi.model.MovimientoStock;
import com.utp.productosapi.repository.MovimientoStockRepository;

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


    // LISTAR TODOS LOS PRODUCTOS
    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return repository.findAll();
    }

    // BUSCAR PRODUCTO POR ID
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // CREAR PRODUCTO
    @Transactional
    public Producto crear(Producto producto) {
        validar(producto);
        producto.setId(null);
        return repository.save(producto);
    }

    // ACTUALIZAR PRODUCTO COMPLETO
    @Transactional
    public Optional<Producto> actualizar(Long id, Producto datos) {
        validar(datos);

        return repository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            existente.setCategoria(datos.getCategoria());
            existente.setPrecio(datos.getPrecio());
            existente.setStock(datos.getStock());

            return repository.save(existente);
        });
    }

    // ACTUALIZAR SOLO EL PRECIO
    @Transactional
    public Optional<Producto> actualizarPrecio(Long id, BigDecimal precio) {
        if (precio == null ||
            precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "El precio debe ser mayor que cero"
            );
        }

        return repository.findById(id).map(existente -> {
            existente.setPrecio(precio);
            return repository.save(existente);
        });
    }

    // ELIMINAR PRODUCTO
    @Transactional
    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    // VALIDAR DATOS DEL PRODUCTO
    private void validar(Producto producto) {

        if (producto.getNombre() == null ||
            producto.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                "El nombre es obligatorio"
            );
        }

        if (producto.getCategoria() == null ||
            producto.getCategoria().isBlank()) {
            throw new IllegalArgumentException(
                "La categoría es obligatoria"
            );
        }

        if (producto.getPrecio() == null ||
            producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "El precio debe ser mayor que cero"
            );
        }

        if (producto.getStock() == null ||
            producto.getStock() < 0) {
            throw new IllegalArgumentException(
                "El stock no puede ser negativo"
            );
        }
    }

     // VALIDAR SALIDA DE INVENTARIO
    
    // BUSCAR PRODUCTOS POR NOMBRE - JPQL
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaNegocioException(
                "El texto de búsqueda no puede estar vacío"
            );
        }

        return repository.buscarPorNombre(texto);
    }

    // BUSCAR PRODUCTOS POR CATEGORÍA
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String categoria) {
        return repository.findByCategoriaIgnoreCase(categoria);
    }

    // BUSCAR PRODUCTOS POR RANGO DE PRECIOS
    @Transactional(readOnly = true)
    public List<Producto> buscarPorRango(
            BigDecimal min, BigDecimal max) {

        if (min == null || max == null ||
                min.compareTo(max) > 0) {
            throw new ReglaNegocioException(
                "El rango de precios no es válido"
            );
        }

        return repository.buscarPorRangoPrecio(min, max);
    }

    // BUSCAR PRODUCTOS CON STOCK BAJO
    @Transactional(readOnly = true)
    public List<Producto> buscarConStockBajo(Integer limite) {
        if (limite == null || limite < 0) {
            throw new ReglaNegocioException(
                "El límite no puede ser negativo"
            );
        }

        return repository.buscarConStockBajo(limite);
    }

    // REGISTRAR SALIDA DE INVENTARIO
    @Transactional
    public Producto registrarSalida(Long productoId, int cantidad) {

        Producto producto = obtenerProducto(productoId);

        validarSalida(producto, cantidad);

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

    
    // REGISTRAR ENTRADA DE INVENTARIO
    @Transactional
    public Producto registrarEntrada(Long productoId, int cantidad) {

        // Validar que la cantidad sea positiva
        if (cantidad <= 0) {
            throw new ReglaNegocioException(
                "La cantidad debe ser mayor que cero"
            );
        }

        // Buscar el producto
        Producto producto = obtenerProducto(productoId);

        // Aumentar el stock
        producto.setStock(producto.getStock() + cantidad);

        // Registrar el movimiento de entrada
        MovimientoStock movimiento = new MovimientoStock(
            producto,
            "ENTRADA",
            cantidad,
            LocalDateTime.now()
        );

        movimientoRepository.save(movimiento);

        return producto;
    }


    // SIMULAR ERROR PARA COMPROBAR ROLLBACK
    @Transactional
    public void simularSalidaConError(Long productoId, int cantidad) {

        Producto producto = obtenerProducto(productoId);

        validarSalida(producto, cantidad);

        producto.setStock(producto.getStock() - cantidad);

        MovimientoStock movimiento = new MovimientoStock(
            producto,
            "SALIDA",
            cantidad,
            LocalDateTime.now()
        );

        movimientoRepository.save(movimiento);

        throw new IllegalStateException(
            "Error simulado: la transacción debe hacer rollback"
        );
    }

    // OBTENER PRODUCTO POR ID
    private Producto obtenerProducto(Long id) {
        return repository.findById(id)
            .orElseThrow(() ->
                new RecursoNoEncontradoException(
                    "Producto no encontrado: " + id
                )
            );
    }


    private void validarSalida(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException(
                "La cantidad debe ser mayor que cero"
            );
        }

        if (producto.getStock() < cantidad) {
            throw new ReglaNegocioException(
                "Stock insuficiente. Disponible: "
                + producto.getStock()
            );
        }
    }

    public long contarProductos() {
       return repository.count();
    }

    
}