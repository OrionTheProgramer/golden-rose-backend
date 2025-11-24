package com.servicios.FoodExpress.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductoResponse {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String imagenUrl;
    private String descripcion;
    private Long categoriaId;
    private String categoriaNombre;
}
