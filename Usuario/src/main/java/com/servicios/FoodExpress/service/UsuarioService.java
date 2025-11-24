package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Usuario;
import com.servicios.FoodExpress.Repository.UsuarioRepository;
import com.servicios.FoodExpress.dto.UsuarioRequest;
import com.servicios.FoodExpress.dto.UsuarioResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UsuarioResponse obtenerPorId(Long id) {
        return toResponse(getById(id));
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        Usuario nuevo = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .role(request.getRole())
                .telefono(request.getTelefono())
                .activo(true)
                .build();
        return toResponse(usuarioRepository.save(nuevo));
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = getById(id);
        usuario.setUsername(request.getUsername());
        usuario.setRole(request.getRole());
        usuario.setTelefono(request.getTelefono());
        if (!usuario.getEmail().equals(request.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
            usuario.setEmail(request.getEmail());
        }
        return toResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponse actualizarRol(Long id, String nuevoRol) {
        Usuario usuario = getById(id);
        usuario.setRole(nuevoRol);
        return toResponse(usuarioRepository.save(usuario));
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario getById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .telefono(usuario.getTelefono())
                .activo(usuario.isActivo())
                .build();
    }
}
