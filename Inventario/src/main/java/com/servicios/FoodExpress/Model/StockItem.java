package com.servicios.FoodExpress.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_items", uniqueConstraints = @UniqueConstraint(name = "uk_stock_product", columnNames = "product_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "product_id")
    private Long productId;

    @NotBlank
    @Size(max = 120)
    private String productName;

    @Min(0)
    private int stock;

    @Min(0)
    private int umbralCritico;
}
