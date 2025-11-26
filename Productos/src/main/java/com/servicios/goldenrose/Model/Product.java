package com.servicios.goldenrose.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    @PositiveOrZero
    private BigDecimal precio;

    /** Ej: Common, Rare, Epic, Legendary, Exclusive */
    private String rareza;

    /** Icono/imagen de rareza (desde valorant-api). */
    private String rarezaIconUrl;

    /** URL remota de imagen (opcional) */
    private String imagenUrl;

    /** Imagen almacenada directamente */
    @Lob
    @Column(name = "imagen_data")
    private byte[] imagenData;

    private String imagenContentType;

    /** UUID opcional de referencia a skin/level en valorant-api. */
    private String referenciaExterna;

    /** Stock disponible */
    private Integer stock;

    private boolean activo = true;

    @CreationTimestamp
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    private LocalDateTime actualizadoEn;
}
