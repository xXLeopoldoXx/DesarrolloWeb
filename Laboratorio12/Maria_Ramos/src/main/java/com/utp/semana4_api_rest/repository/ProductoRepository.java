package com.utp.semana4_api_rest.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.utp.semana4_api_rest.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Consulta derivada: buscar por categoria
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    // JPQL: buscar productos por nombre
    @Query("""
        SELECT p
        FROM Producto p
        WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
        ORDER BY p.nombre
        """)
    List<Producto> buscarPorNombre(@Param("texto") String texto);

    // JPQL: buscar productos por rango de precios
    @Query("""
        SELECT p
        FROM Producto p
        WHERE p.precio BETWEEN :min AND :max
        ORDER BY p.precio ASC
        """)
    List<Producto> buscarPorRangoPrecio(
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max);

    // JPQL: buscar productos con stock bajo
    @Query("""
        SELECT p
        FROM Producto p
        WHERE p.stock < :limite
        ORDER BY p.stock ASC
        """)
    List<Producto> buscarConStockBajo(@Param("limite") Integer limite);

    // Contar productos con stock bajo
    long countByStockLessThan(int stock);
}

