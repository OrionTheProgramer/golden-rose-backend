package com.servicios.FoodExpress.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String telefono;
    private boolean activo;
}
