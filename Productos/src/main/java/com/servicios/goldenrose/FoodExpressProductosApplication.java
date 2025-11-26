package com.servicios.goldenrose;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada del microservicio de Productos (CRUD). */
@SpringBootApplication
public class FoodExpressProductosApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodExpressProductosApplication.class, args);
    }
}
