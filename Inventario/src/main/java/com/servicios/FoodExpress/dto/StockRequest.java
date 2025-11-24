package com.servicios.FoodExpress.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StockRequest {
    @NotNull
    private Long productId;
    @NotBlank
    @Size(max = 120)
    private String productName;
    @Min(0)
    private int stock;
    @Min(0)
    private int umbralCritico;
}
