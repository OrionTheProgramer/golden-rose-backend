package com.servicios.FoodExpress.Controller;

import com.servicios.FoodExpress.dto.CartItemRequest;
import com.servicios.FoodExpress.dto.CartResponse;
import com.servicios.FoodExpress.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API REST del carrito por usuario. Todos los endpoints son stateless, reciben userId.
 */
@RestController
@RequestMapping("/api/carritos")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /** Obtiene o crea un carrito vacio para el usuario. */
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> obtener(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.obtenerPorUsuario(userId));
    }

    /** Agrega un producto o acumula su cantidad. */
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> agregar(@PathVariable Long userId, @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.agregarItem(userId, request));
    }

    /** Ajusta la cantidad de un item existente. */
    @PatchMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> actualizarCantidad(@PathVariable Long userId, @PathVariable Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.actualizarCantidad(userId, productId, quantity));
    }

    /** Elimina un producto especifico del carrito. */
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> eliminarItem(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.eliminarItem(userId, productId));
    }

    /** Limpia el carrito completo. */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void limpiar(@PathVariable Long userId) {
        cartService.limpiar(userId);
    }
}
