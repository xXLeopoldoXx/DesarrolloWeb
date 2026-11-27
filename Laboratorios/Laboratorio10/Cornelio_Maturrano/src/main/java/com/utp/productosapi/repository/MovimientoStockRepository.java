package com.utp.productosapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.productosapi.model.MovimientoStock;

public interface MovimientoStockRepository
        extends JpaRepository<MovimientoStock, Long> {
}
