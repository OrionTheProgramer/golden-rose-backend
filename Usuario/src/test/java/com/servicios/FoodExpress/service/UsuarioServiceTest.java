package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Usuario;
import com.servicios.FoodExpress.Repository.UsuarioRepository;
import com.servicios.FoodExpress.dto.UsuarioRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearUsuario_ok() {
        UsuarioRequest req = buildRequest();
        when(usuarioRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        var resp = usuarioService.crear(req);

        assertEquals(1L, resp.getId());
        assertEquals("user", resp.getUsername());
        assertEquals("client", resp.getRole());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void crearUsuario_emailDuplicado() {
        UsuarioRequest req = buildRequest();
        when(usuarioRepository.existsByEmail("user@test.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> usuarioService.crear(req));
    }

    @Test
    void actualizarUsuario_cambiaEmail() {
        Usuario existente = Usuario.builder()
                .id(5L)
                .username("old")
                .email("old@test.com")
                .role("client")
                .telefono("123")
                .activo(true)
                .build();
        UsuarioRequest req = buildRequest();

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = usuarioService.actualizar(5L, req);

        assertEquals("user@test.com", resp.getEmail());
        assertEquals("user", resp.getUsername());
    }

    @Test
    void eliminarUsuario_noExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> usuarioService.eliminar(99L));
    }

    private UsuarioRequest buildRequest() {
        UsuarioRequest req = new UsuarioRequest();
        req.setUsername("user");
        req.setEmail("user@test.com");
        req.setRole("client");
        req.setTelefono("123456789");
        return req;
    }
}
