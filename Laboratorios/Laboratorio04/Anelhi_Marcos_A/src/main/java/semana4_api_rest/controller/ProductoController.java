package semana4_api_rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import semana4_api_rest.dto.ProductoRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductoController {

    @GetMapping("/productos")
    public List<String> listarProductos() {
        return List.of("Laptop Lenovo", "Mouse Logitech", "Silla ergonómica");
    }
}