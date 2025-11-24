package com.servicios.FoodExpress.dto;

import com.servicios.FoodExpress.Model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrdenRequest {
    @NotBlank
    @Size(max = 120)
    private String customerName;

    @Email
    @Size(max = 120)
    private String customerEmail;

    @NotNull
    private OrderStatus status;

    @Valid
    @NotNull
    private List<OrderItemRequest> items;
}
