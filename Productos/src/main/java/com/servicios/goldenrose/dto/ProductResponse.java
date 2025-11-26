package com.servicios.goldenrose.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Respuesta expuesta hacia el frontend con datos completos del producto. */
@Data
@Builder
@AllArgsConstructor
public class ProductResponse extends RepresentationModel<ProductResponse> {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String rareza;
    private String rarezaIconUrl;
    private String imagenUrl;
    private boolean hasImageData;
    private String referenciaExterna;
    private Integer stock;
    private boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
