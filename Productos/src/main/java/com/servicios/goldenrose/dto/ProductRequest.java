package com.servicios.goldenrose.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/** Payload recibido al crear/actualizar un producto. */
@Data
public class ProductRequest {
    @NotBlank
    private String nombre;
    private String descripcion;
    @NotNull
    @PositiveOrZero
    private BigDecimal precio;
    private String rareza;
    private String imagenUrl;
    private Integer stock;
    private Boolean activo;
}
