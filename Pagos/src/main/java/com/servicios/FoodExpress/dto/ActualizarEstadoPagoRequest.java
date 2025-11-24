package com.servicios.FoodExpress.dto;

import com.servicios.FoodExpress.Model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoPagoRequest {
    @NotNull
    private PaymentStatus status;
    private String providerReference;
}
