package com.utp.semana4_api_rest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.semana4_api_rest.dto.ResumenBackendResponse;
import com.utp.semana4_api_rest.repository.MovimientoStockRepository;
import com.utp.semana4_api_rest.repository.ProductoRepository;

@Service
public class AdminResumenService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;

    public AdminResumenService(
            ProductoRepository productoRepository,
            MovimientoStockRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public ResumenBackendResponse obtenerResumen() {

        long totalProductos = productoRepository.count();
        long productosStockBajo =
                productoRepository.countByStockLessThan(5);
        long totalMovimientos = movimientoRepository.count();

        return new ResumenBackendResponse(
                totalProductos,
                productosStockBajo,
                totalMovimientos,
                "BACKEND_OPERATIVO"
        );
    }
}
