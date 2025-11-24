package com.servicios.FoodExpress.Model;

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

    /** Categor�a o tipo de arma */
    private String categoria;

    /** URL remota de imagen (se usa si no hay imagenData) */
    private String imagenUrl;

    /** Imagen almacenada en base de datos */
    @Lob
    private byte[] imagenData;

    private String imagenContentType;

    private boolean activo = true;

    /** Id externo de Valorant API (skinUuid) para referencia */
    private String referenciaExterna;

    @CreationTimestamp
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    private LocalDateTime actualizadoEn;
}
