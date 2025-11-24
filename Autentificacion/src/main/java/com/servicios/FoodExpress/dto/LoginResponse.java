package com.servicios.FoodExpress.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String role;
    private String username;
    private String email;
}
