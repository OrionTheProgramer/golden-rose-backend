package com.servicios.FoodExpress.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Documentacion OpenAPI/Swagger del catalogo. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("API Catalogo - Golden Rose")
                        .version("1.0.0")
                        .description("Endpoints de productos y categorias (lectura y fallback)"));
    }
}
