package com.servicios.FoodExpress.Repository;

import com.servicios.FoodExpress.Model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    Optional<Orden> findByOrderNumber(String orderNumber);
    boolean existsByOrderNumber(String orderNumber);
}
