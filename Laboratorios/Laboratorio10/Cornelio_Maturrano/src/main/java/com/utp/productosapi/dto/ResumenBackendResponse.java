
package com.utp.productosapi.dto;

public record ResumenBackendResponse(
        long totalProductos,
        long productosConStockBajo,
        long totalMovimientos,
        String estado) {
}
