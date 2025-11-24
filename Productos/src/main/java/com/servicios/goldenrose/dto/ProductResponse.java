package com.servicios.goldenrose.dto;

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
    private String imagenUrl;
    private Integer stock;
    private boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
