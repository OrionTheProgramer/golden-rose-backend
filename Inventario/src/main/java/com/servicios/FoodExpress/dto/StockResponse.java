package com.servicios.FoodExpress.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockResponse {
    private Long id;
    private Long productId;
    private String productName;
    private int stock;
    private int umbralCritico;
    private boolean critico;
}
