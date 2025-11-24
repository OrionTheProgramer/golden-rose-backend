package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Payment;
import com.servicios.FoodExpress.Model.PaymentStatus;
import com.servicios.FoodExpress.Repository.PaymentRepository;
import com.servicios.FoodExpress.dto.ActualizarEstadoPagoRequest;
import com.servicios.FoodExpress.dto.PaymentRequest;
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
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearPago_ok() {
        PaymentRequest req = new PaymentRequest();
        req.setOrderId(1L);
        req.setOrderNumber("GR-123456");
        req.setAmount(BigDecimal.valueOf(50));
        req.setCustomerEmail("test@test.com");

        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = paymentService.crear(req);

        assertEquals("GR-123456", resp.getOrderNumber());
        assertEquals(PaymentStatus.PENDIENTE, resp.getStatus());
        assertNotNull(resp.getProviderReference());
    }

    @Test
    void actualizarEstado_noExiste() {
        when(paymentRepository.findByOrderId(99L)).thenReturn(Optional.empty());

        ActualizarEstadoPagoRequest req = new ActualizarEstadoPagoRequest();
        req.setStatus(PaymentStatus.APROBADO);

        assertThrows(EntityNotFoundException.class, () -> paymentService.actualizarEstado(99L, req));
    }
}
