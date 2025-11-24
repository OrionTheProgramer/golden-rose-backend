package com.servicios.FoodExpress.Repository;

import com.servicios.FoodExpress.Model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<StockItem, Long> {
    Optional<StockItem> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
    List<StockItem> findByStockLessThanEqual(int stock);
}
