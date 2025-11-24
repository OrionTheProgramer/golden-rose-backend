package com.servicios.FoodExpress.Controller;

import com.servicios.FoodExpress.dto.ActualizarEstadoPagoRequest;
import com.servicios.FoodExpress.dto.PaymentRequest;
import com.servicios.FoodExpress.dto.PaymentResponse;
import com.servicios.FoodExpress.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> listar() {
        return ResponseEntity.ok(paymentService.listar());
    }

    @GetMapping("/orden/{orderId}")
    public ResponseEntity<PaymentResponse> obtenerPorOrden(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.obtenerPorOrden(orderId));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> crear(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.crear(request));
    }

    @PatchMapping("/orden/{orderId}/estado")
    public ResponseEntity<PaymentResponse> actualizarEstado(@PathVariable Long orderId, @Valid @RequestBody ActualizarEstadoPagoRequest request) {
        return ResponseEntity.ok(paymentService.actualizarEstado(orderId, request));
    }
}
