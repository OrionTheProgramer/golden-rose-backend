package com.servicios.FoodExpress.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Metadatos OpenAPI/Swagger del microservicio de pagos. */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("API Pagos - Golden Rose")
                        .version("1.0.0")
                        .description("Endpoints para registrar pagos y actualizar estados"));
    }
}
