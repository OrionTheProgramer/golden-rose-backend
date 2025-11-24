package com.servicios.FoodExpress.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarRolRequest {
    @NotBlank
    @Size(max = 40)
    private String role;
}
