package com.utp.tienda.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.utp.tienda.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Consulta derivada por categoría
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    // Consulta personalizada JPQL con búsqueda parcial
    @Query("""
        SELECT p FROM Producto p
        WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
        ORDER BY p.nombre
    """)
    List<Producto> buscarPorNombre(@Param("texto") String texto);

    // Consulta personalizada JPQL por rango de precios
    @Query("""
        SELECT p FROM Producto p
        WHERE p.precio BETWEEN :min AND :max
        ORDER BY p.precio ASC
    """)
    List<Producto> buscarPorRangoPrecio(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    // Consulta personalizada JPQL para productos con stock bajo
    @Query("SELECT p FROM Producto p WHERE p.stock <= :limite ORDER BY p.stock ASC")
    List<Producto> buscarConStockBajo(@Param("limite") Integer limite);
}