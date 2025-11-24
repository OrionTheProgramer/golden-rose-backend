package com.servicios.FoodExpress.dto;

import com.servicios.FoodExpress.Model.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull
    private Long orderId;
    @NotBlank
    @Size(max = 50)
    private String orderNumber;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;
    @Email
    @Size(max = 120)
    private String customerEmail;
    private PaymentStatus status = PaymentStatus.PENDIENTE;
}
