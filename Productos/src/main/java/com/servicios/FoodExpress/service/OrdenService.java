package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.OrderItem;
import com.servicios.FoodExpress.Model.OrderStatus;
import com.servicios.FoodExpress.Model.Orden;
import com.servicios.FoodExpress.Repository.OrdenRepository;
import com.servicios.FoodExpress.dto.OrderItemRequest;
import com.servicios.FoodExpress.dto.OrderItemResponse;
import com.servicios.FoodExpress.dto.OrdenRequest;
import com.servicios.FoodExpress.dto.OrdenResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdenService {

    private final OrdenRepository ordenRepository;

    public List<OrdenResponse> listar() {
        return ordenRepository.findAll().stream().map(this::toResponse).toList();
    }

    public OrdenResponse obtener(Long id) {
        return toResponse(getById(id));
    }

    public OrdenResponse crear(OrdenRequest request) {
        Orden orden = new Orden();
        orden.setOrderNumber(generarNumeroOrden());
        orden.setCustomerName(request.getCustomerName());
        orden.setCustomerEmail(request.getCustomerEmail());
        orden.setStatus(request.getStatus());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            OrderItem item = OrderItem.builder()
                    .productId(itemReq.getProductId())
                    .productName(itemReq.getProductName())
                    .quantity(itemReq.getQuantity())
                    .price(itemReq.getPrice())
                    .build();
            orden.addItem(item);
            total = total.add(itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }
        orden.setTotal(total);

        return toResponse(ordenRepository.save(orden));
    }

    public OrdenResponse actualizarEstado(Long id, OrderStatus status) {
        Orden orden = getById(id);
        orden.setStatus(status);
        return toResponse(ordenRepository.save(orden));
    }

    public void eliminar(Long id) {
        if (!ordenRepository.existsById(id)) {
            throw new EntityNotFoundException("Orden no encontrada");
        }
        ordenRepository.deleteById(id);
    }

    private Orden getById(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));
    }

    private String generarNumeroOrden() {
        return "GR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrdenResponse toResponse(Orden orden) {
        List<OrderItemResponse> items = orden.getItems().stream().map(oi -> OrderItemResponse.builder()
                .id(oi.getId())
                .productId(oi.getProductId())
                .productName(oi.getProductName())
                .quantity(oi.getQuantity())
                .price(oi.getPrice())
                .build()).toList();

        return OrdenResponse.builder()
                .id(orden.getId())
                .orderNumber(orden.getOrderNumber())
                .customerName(orden.getCustomerName())
                .customerEmail(orden.getCustomerEmail())
                .status(orden.getStatus())
                .total(orden.getTotal())
                .createdAt(orden.getCreatedAt())
                .items(items)
                .build();
    }
}
