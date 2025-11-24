package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Payment;
import com.servicios.FoodExpress.Model.PaymentStatus;
import com.servicios.FoodExpress.Repository.PaymentRepository;
import com.servicios.FoodExpress.dto.ActualizarEstadoPagoRequest;
import com.servicios.FoodExpress.dto.PaymentRequest;
import com.servicios.FoodExpress.dto.PaymentResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<PaymentResponse> listar() {
        return paymentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PaymentResponse obtenerPorOrden(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado para la orden"));
    }

    public PaymentResponse crear(PaymentRequest request) {
        Payment pago = Payment.builder()
                .orderId(request.getOrderId())
                .orderNumber(request.getOrderNumber())
                .amount(request.getAmount())
                .customerEmail(request.getCustomerEmail())
                .status(request.getStatus() != null ? request.getStatus() : PaymentStatus.PENDIENTE)
                .providerReference("SIM-" + UUID.randomUUID().toString().substring(0, 8))
                .build();
        return toResponse(paymentRepository.save(pago));
    }

    public PaymentResponse actualizarEstado(Long orderId, ActualizarEstadoPagoRequest request) {
        Payment pago = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado para la orden"));
        pago.setStatus(request.getStatus());
        if (request.getProviderReference() != null) {
            pago.setProviderReference(request.getProviderReference());
        }
        return toResponse(paymentRepository.save(pago));
    }

    private PaymentResponse toResponse(Payment pago) {
        return PaymentResponse.builder()
                .id(pago.getId())
                .orderId(pago.getOrderId())
                .orderNumber(pago.getOrderNumber())
                .amount(pago.getAmount())
                .customerEmail(pago.getCustomerEmail())
                .status(pago.getStatus())
                .providerReference(pago.getProviderReference())
                .createdAt(pago.getCreatedAt())
                .updatedAt(pago.getUpdatedAt())
                .build();
    }
}
