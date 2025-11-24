package com.servicios.FoodExpress.dto;

import com.servicios.FoodExpress.Model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private BigDecimal amount;
    private String customerEmail;
    private PaymentStatus status;
    private String providerReference;
    private Instant createdAt;
    private Instant updatedAt;
}
