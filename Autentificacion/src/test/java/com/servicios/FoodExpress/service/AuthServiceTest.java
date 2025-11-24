package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.AuthUser;
import com.servicios.FoodExpress.Repository.AuthUserRepository;
import com.servicios.FoodExpress.dto.LoginRequest;
import com.servicios.FoodExpress.dto.RegisterRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ok() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@test.com");
        req.setPassword("pass");

        AuthUser user = AuthUser.builder()
                .email("user@test.com")
                .password("hashed")
                .role("client")
                .username("user")
                .build();

        when(authUserRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("user@test.com")).thenReturn("token");

        var resp = authService.login(req);
        assertEquals("token", resp.getToken());
        assertEquals("client", resp.getRole());
    }

    @Test
    void login_credencialesInvalidas() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@test.com");
        req.setPassword("pass");

        when(authUserRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.login(req));
    }

    @Test
    void register_ok() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("client@test.com");
        req.setUsername("user");
        req.setPassword("pass123");

        when(authUserRepository.existsByEmail("client@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");
        when(jwtUtil.generateToken("client@test.com")).thenReturn("token");
        when(authUserRepository.save(any(AuthUser.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = authService.register(req);
        assertEquals("token", resp.getToken());
        assertEquals("client", resp.getRole());
    }
}
