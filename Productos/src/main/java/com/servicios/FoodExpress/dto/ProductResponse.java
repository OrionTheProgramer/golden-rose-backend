package com.servicios.FoodExpress.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String rareza;
    private String categoria;
    private String imagenUrl;
    private boolean hasImageData;
    private String imagenContentType;
    private boolean activo;
    private String referenciaExterna;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
