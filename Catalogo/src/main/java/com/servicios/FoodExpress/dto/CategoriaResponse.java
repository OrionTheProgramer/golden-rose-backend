package com.servicios.FoodExpress.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaResponse {
    private Long id;
    private String nombre;
}
