package com.servicios.FoodExpress.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(max = 80)
    private String username;
    @Email
    @NotBlank
    @Size(max = 120)
    private String email;
    @NotBlank
    @Size(min = 6)
    private String password;
}
