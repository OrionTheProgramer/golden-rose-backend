package com.servicios.FoodExpress.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemRequest {
    @NotNull
    private Long productId;
    @NotBlank
    @Size(max = 120)
    private String productName;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;
    @Min(1)
    private int quantity;
}
