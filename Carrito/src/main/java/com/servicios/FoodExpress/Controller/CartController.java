package com.servicios.FoodExpress.Controller;

import com.servicios.FoodExpress.dto.CartItemRequest;
import com.servicios.FoodExpress.dto.CartResponse;
import com.servicios.FoodExpress.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carritos")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> obtener(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.obtenerPorUsuario(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> agregar(@PathVariable Long userId, @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.agregarItem(userId, request));
    }

    @PatchMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> actualizarCantidad(@PathVariable Long userId, @PathVariable Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.actualizarCantidad(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> eliminarItem(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.eliminarItem(userId, productId));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void limpiar(@PathVariable Long userId) {
        cartService.limpiar(userId);
    }
}
