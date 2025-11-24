package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.AuthUser;
import com.servicios.FoodExpress.Repository.AuthUserRepository;
import com.servicios.FoodExpress.dto.LoginRequest;
import com.servicios.FoodExpress.dto.LoginResponse;
import com.servicios.FoodExpress.dto.RegisterRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        AuthUser user = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Credenciales inválidas"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new EntityNotFoundException("Credenciales inválidas");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return LoginResponse.builder()
                .token(token)
                .role(user.getRole())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public LoginResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        AuthUser user = AuthUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getEmail().endsWith("profesor.duoc.cl") ? "admin" : "client")
                .build();
        authUserRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return LoginResponse.builder()
                .token(token)
                .role(user.getRole())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
