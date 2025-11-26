package com.servicios.FoodExpress.Controller;

import com.servicios.FoodExpress.dto.ActualizarRolRequest;
import com.servicios.FoodExpress.dto.UsuarioRequest;
import com.servicios.FoodExpress.dto.UsuarioResponse;
import com.servicios.FoodExpress.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API REST para CRUD de usuarios y cambio de roles. */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /** Lista usuarios. */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    /** Obtiene usuario por id. */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    /** Crea usuario validando email unico. */
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    /** Actualiza datos basicos del usuario. */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    /** Cambia el rol del usuario. */
    @PatchMapping("/{id}/rol")
    public ResponseEntity<UsuarioResponse> actualizarRol(@PathVariable Long id, @Valid @RequestBody ActualizarRolRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarRol(id, request.getRole()));
    }

    /** Elimina usuario. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}
