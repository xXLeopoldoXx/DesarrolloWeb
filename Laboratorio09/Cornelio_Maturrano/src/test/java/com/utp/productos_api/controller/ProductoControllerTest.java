
package com.utp.productosapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.utp.productosapi.model.Producto;
import com.utp.productosapi.service.ProductoService;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    @Test
    void postDebeRetornar201() throws Exception {

        Producto producto = new Producto(
            "Laptop",
            "Tecnologia",
            new BigDecimal("3500.00"),
            10
        );

        producto.setId(1L);

        when(service.crear(any(Producto.class)))
            .thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nombre": "Laptop",
                        "categoria": "Tecnologia",
                        "precio": 3500.00,
                        "stock": 10
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.categoria").value("Tecnologia"))
                .andExpect(jsonPath("$.precio").value(3500.00))
                .andExpect(jsonPath("$.stock").value(10));
    }
}
