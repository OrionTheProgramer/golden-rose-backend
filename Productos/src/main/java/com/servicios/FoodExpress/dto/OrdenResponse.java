package com.servicios.FoodExpress.dto;

import com.servicios.FoodExpress.Model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrdenResponse {
    private Long id;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private OrderStatus status;
    private BigDecimal total;
    private Instant createdAt;
    private List<OrderItemResponse> items;
}
