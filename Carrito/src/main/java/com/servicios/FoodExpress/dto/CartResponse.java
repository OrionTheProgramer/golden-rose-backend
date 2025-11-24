package com.servicios.FoodExpress.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long id;
    private Long userId;
    private BigDecimal total;
    private List<CartItemResponse> items;
}
