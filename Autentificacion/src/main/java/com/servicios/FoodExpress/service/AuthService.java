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

/**
 * Servicio central de autenticacion: valida credenciales, registra usuarios y genera tokens JWT.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Valida las credenciales del usuario y emite un JWT basico con el email como subject.
     *
     * @param request credenciales enviadas por el cliente
     * @return datos del usuario autenticado junto al token
     * @throws EntityNotFoundException si el correo no existe o la clave no coincide
     */
    public LoginResponse login(LoginRequest request) {
        AuthUser user = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Credenciales invalidas"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new EntityNotFoundException("Credenciales invalidas");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return LoginResponse.builder()
                .id(user.getId())
                .token(token)
                .role(user.getRole())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    /**
     * Registra un nuevo usuario y devuelve el token de acceso inmediato.
     * Asigna rol admin si el correo termina en profesor.duoc.cl, de lo contrario client.
     *
     * @param request datos de registro
     * @return datos del nuevo usuario con su token JWT
     * @throws IllegalArgumentException si el correo ya esta registrado
     */
    public LoginResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El correo ya esta registrado");
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
                .id(user.getId())
                .token(token)
                .role(user.getRole())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
