package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Cart;
import com.servicios.FoodExpress.Model.CartItem;
import com.servicios.FoodExpress.Repository.CartRepository;
import com.servicios.FoodExpress.dto.CartItemRequest;
import com.servicios.FoodExpress.dto.CartItemResponse;
import com.servicios.FoodExpress.dto.CartResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lógica de negocio del carrito: agrega, actualiza y calcula totales por usuario.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;

    /** Obtiene el carrito del usuario o lo crea en blanco si no existe. */
    public CartResponse obtenerPorUsuario(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .userId(userId)
                        .total(BigDecimal.ZERO)
                        .items(new java.util.ArrayList<>())
                        .build()));
        return toResponse(cart);
    }

    /** Agrega un item (o suma cantidades) y recalcula el total. */
    public CartResponse agregarItem(Long userId, CartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> Cart.builder()
                        .userId(userId)
                        .total(BigDecimal.ZERO)
                        .items(new java.util.ArrayList<>())
                        .build());

        boolean found = false;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(request.getProductId())) {
                item.setQuantity(item.getQuantity() + request.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
            CartItem newItem = CartItem.builder()
                    .productId(request.getProductId())
                    .productName(request.getProductName())
                    .price(request.getPrice())
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(newItem);
        }

        cart.setTotal(calcularTotal(cart));
        return toResponse(cartRepository.save(cart));
    }

    /** Actualiza la cantidad de un item; si queda en cero lo elimina. */
    public CartResponse actualizarCantidad(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }
        cart.setTotal(calcularTotal(cart));
        return toResponse(cartRepository.save(cart));
    }

    /** Elimina un item concreto y recalcula total. */
    public CartResponse eliminarItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado"));
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        cart.setTotal(calcularTotal(cart));
        return toResponse(cartRepository.save(cart));
    }

    /** Limpia el carrito completo del usuario. */
    public void limpiar(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado"));
        cart.getItems().clear();
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    /** Multiplica precio x cantidad y suma todos los items. */
    private BigDecimal calcularTotal(Cart cart) {
        return cart.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(ci -> CartItemResponse.builder()
                .id(ci.getId())
                .productId(ci.getProductId())
                .productName(ci.getProductName())
                .price(ci.getPrice())
                .quantity(ci.getQuantity())
                .build()).toList();
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .total(cart.getTotal() != null ? cart.getTotal() : BigDecimal.ZERO)
                .items(items)
                .build();
    }
}
