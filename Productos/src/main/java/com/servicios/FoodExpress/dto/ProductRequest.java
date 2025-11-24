package com.servicios.FoodExpress.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank
    private String nombre;
    private String descripcion;
    @NotNull
    @PositiveOrZero
    private BigDecimal precio;
    private String rareza;
    private String categoria;
    private String imagenUrl;
    private String imagenBase64;
    private String imagenContentType;
    private Boolean activo;
    /** UUID de la skin en valorant-api.com (opcional) */
    private String referenciaExterna;
}
