package com.servicios.FoodExpress.dto;

import com.servicios.FoodExpress.Model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoRequest {
    @NotNull
    private OrderStatus status;
}
