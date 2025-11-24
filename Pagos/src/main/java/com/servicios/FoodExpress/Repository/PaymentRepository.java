package com.servicios.FoodExpress.Repository;

import com.servicios.FoodExpress.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByOrderNumber(String orderNumber);
    List<Payment> findByStatus(com.servicios.FoodExpress.Model.PaymentStatus status);
}
