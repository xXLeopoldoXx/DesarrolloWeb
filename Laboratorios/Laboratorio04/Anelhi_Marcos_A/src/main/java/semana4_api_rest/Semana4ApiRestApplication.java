package semana4_api_rest;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Semana4ApiRestApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Semana4ApiRestApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        app.run(args);
    }
}