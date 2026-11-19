package com.utp.semana2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import com.utp.semana2.dto.ProductoRequest;
import com.utp.semana2.exception.ProductoNoEncontradoException;
import com.utp.semana2.model.Producto;

@Service
public class ProductoService {
    private final Map<Long, Producto> productos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    public ProductoService() {
        crear(new ProductoRequest("Laptop Lenovo", 3500.00, 8));
        crear(new ProductoRequest("Mouse Logitech", 80.00, 25));
        crear(new ProductoRequest("Teclado mecánico", 240.00, 12));
        // Punto 5: Agregar un producto inicial adicional
        crear(new ProductoRequest("Monitor LG 24 pulgadas", 550.00, 4));
    }

    public List<Producto> listar() {
        return new ArrayList<>(productos.values());
    }

    public Producto buscarPorId(Long id) {
        Producto producto = productos.get(id);
        if (producto == null) {
            throw new ProductoNoEncontradoException(id);
        }
        return producto;
    }

    public List<Producto> buscarPorNombre(String nombre) {
        String texto = nombre.toLowerCase();
        return productos.values().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(texto))
                .toList();
    }

    // Punto 1: Lógica para listar productos con stock menor o igual al límite
    public List<Producto> buscarPorStockBajo(int limite) {
        return productos.values().stream()
                .filter(p -> p.getStock() <= limite)
                .toList();
    }

    // Punto 2: Lógica para filtrar productos por precio mayor
    public List<Producto> buscarPorPrecioMayor(double precio) {
        return productos.values().stream()
                .filter(p -> p.getPrecio() > precio)
                .toList();
    }

    public Producto crear(ProductoRequest request) {
        validar(request);
        Long nuevoId = secuencia.incrementAndGet();
        Producto producto = new Producto(
                nuevoId,
                request.nombre(),
                request.precio(),
                request.stock()
        );
        productos.put(nuevoId, producto);
        return producto;
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        validar(request);
        Producto producto = buscarPorId(id);
        producto.setNombre(request.nombre());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        return producto;
    }

    public void eliminar(Long id) {
        Producto eliminado = productos.remove(id);
        if (eliminado == null) {
            throw new ProductoNoEncontradoException(id);
        }
    }

    private void validar(ProductoRequest request) {
        // Punto 3: Validación para que el nombre tenga al menos 3 caracteres
        if (request.nombre() == null || request.nombre().trim().length() < 3) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio y debe tener al menos 3 caracteres.");
        }
        if (request.precio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }
        if (request.stock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
    }
}