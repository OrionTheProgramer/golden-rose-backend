package com.servicios.FoodExpress.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {
    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal precio;

    @Size(max = 255)
    private String imagenUrl;

    @Size(max = 255)
    private String descripcion;

    @NotNull
    private Long categoriaId;
}
