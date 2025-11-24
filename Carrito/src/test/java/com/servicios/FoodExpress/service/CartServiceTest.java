package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Cart;
import com.servicios.FoodExpress.Model.CartItem;
import com.servicios.FoodExpress.Repository.CartRepository;
import com.servicios.FoodExpress.dto.CartItemRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void agregarItem_creaCarrito() {
        CartItemRequest req = buildRequest();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = cartService.agregarItem(1L, req);

        assertEquals(1, resp.getItems().size());
        assertEquals(BigDecimal.valueOf(20), resp.getTotal());
    }

    @Test
    void actualizarCantidad_itemNoExiste() {
        Cart cart = Cart.builder().userId(1L).build();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThrows(EntityNotFoundException.class, () -> cartService.actualizarCantidad(1L, 5L, 1));
    }

    private CartItemRequest buildRequest() {
        CartItemRequest req = new CartItemRequest();
        req.setProductId(10L);
        req.setProductName("Skin");
        req.setPrice(BigDecimal.valueOf(10));
        req.setQuantity(2);
        return req;
    }
}
