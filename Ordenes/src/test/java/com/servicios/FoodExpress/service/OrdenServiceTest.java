package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.OrderStatus;
import com.servicios.FoodExpress.Model.Orden;
import com.servicios.FoodExpress.Repository.OrdenRepository;
import com.servicios.FoodExpress.dto.OrderItemRequest;
import com.servicios.FoodExpress.dto.OrdenRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;

    @InjectMocks
    private OrdenService ordenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearOrden_calculaTotal() {
        OrdenRequest request = new OrdenRequest();
        request.setCustomerName("Cliente");
        request.setCustomerEmail("cliente@test.com");
        request.setStatus(OrderStatus.PENDIENTE);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setProductName("Vandal");
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(10));
        request.setItems(List.of(item));

        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = ordenService.crear(request);

        assertEquals(BigDecimal.valueOf(20), resp.getTotal());
        assertEquals(1, resp.getItems().size());
        assertEquals("Vandal", resp.getItems().get(0).getProductName());
    }

    @Test
    void actualizarEstado_noExiste() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> ordenService.actualizarEstado(99L, OrderStatus.PAGADO));
    }
}
