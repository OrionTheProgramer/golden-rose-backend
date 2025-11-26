package com.servicios.FoodExpress.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Documentacion OpenAPI/Swagger del microservicio de usuarios. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("API Usuarios - Golden Rose")
                        .version("1.0.0")
                        .description("Endpoints para gestion de usuarios y perfiles"));
    }
}
