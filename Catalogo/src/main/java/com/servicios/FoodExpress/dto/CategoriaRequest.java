package com.servicios.FoodExpress.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaRequest {
    @NotBlank
    @Size(max = 80)
    private String nombre;
}
