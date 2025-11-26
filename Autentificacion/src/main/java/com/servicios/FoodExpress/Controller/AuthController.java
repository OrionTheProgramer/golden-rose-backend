package com.servicios.FoodExpress.Controller;

import com.servicios.FoodExpress.dto.LoginRequest;
import com.servicios.FoodExpress.dto.LoginResponse;
import com.servicios.FoodExpress.dto.RegisterRequest;
import com.servicios.FoodExpress.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de autenticacion para login y registro.
 * Devuelve JWTs que consumen los demas microservicios.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Autentica credenciales y retorna token + datos basicos. */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /** Registra usuario y entrega token listo para usar. */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
}
