package com.servicios.FoodExpress.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {
    @NotNull
    private Long productId;
    @NotBlank
    @Size(max = 120)
    private String productName;
    @Min(1)
    private int quantity;
    @NotNull
    private BigDecimal price;
}
