package com.utp.semana4_api_rest.dto;

public record ResumenBackendResponse(
        long totalProductos,
        long productosStockBajo,
        long totalMovimientos,
        String estado
) {
}
