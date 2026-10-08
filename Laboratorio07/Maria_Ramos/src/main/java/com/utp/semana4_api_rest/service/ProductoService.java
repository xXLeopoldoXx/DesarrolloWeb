package com.utp.semana4_api_rest.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.semana4_api_rest.dto.ActualizarStockRequest;
import com.utp.semana4_api_rest.dto.ProductoRequest;
import com.utp.semana4_api_rest.exception.ProductoNoEncontradoException;
import com.utp.semana4_api_rest.model.Producto;
import com.utp.semana4_api_rest.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    // LISTAR PRODUCTOS
    @Transactional(readOnly = true)
    public List<Producto> listar(String categoria) {

        return repository.findAll()
                .stream()
                .filter(producto -> categoria == null
                        || producto.getCategoria().equalsIgnoreCase(categoria))
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    // BUSCAR PRODUCTO POR ID
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    // CREAR PRODUCTO UTILIZANDO ProductoRequest
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

        // PostgreSQL generará el ID
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

    // ACTUALIZAR SOLO EL STOCK
    @Transactional
    public Producto actualizarStock(
            Long id,
            ActualizarStockRequest request) {

        Producto producto = buscarPorId(id);

        producto.setStock(request.getStock());

        validar(producto);

        return repository.save(producto);
    }

    // ACTUALIZAR SOLO EL PRECIO
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

    // BUSCAR PRODUCTOS POR NOMBRE
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {

        return repository.findAll()
                .stream()
                .filter(producto -> producto.getNombre()
                        .toLowerCase()
                        .contains(nombre.toLowerCase()))
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    // ELIMINAR PRODUCTO
    @Transactional
    public void eliminar(Long id) {

        if (!repository.existsById(id)) {
            throw new ProductoNoEncontradoException(id);
        }

        repository.deleteById(id);
    }

    // VALIDACIONES
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