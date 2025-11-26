package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.StockItem;
import com.servicios.FoodExpress.Repository.StockRepository;
import com.servicios.FoodExpress.dto.StockRequest;
import com.servicios.FoodExpress.dto.StockResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Servicio de inventario: gestiona stock por producto y umbrales criticos. */
@Service
@RequiredArgsConstructor
@Transactional
public class StockService {

    private final StockRepository stockRepository;

    /** Lista todos los registros de stock. */
    public List<StockResponse> listar() {
        return stockRepository.findAll().stream().map(this::toResponse).toList();
    }

    /** Obtiene el stock de un producto por id. */
    public StockResponse obtenerPorProducto(Long productId) {
        StockItem item = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Stock no encontrado para el producto"));
        return toResponse(item);
    }

    /** Crea o actualiza stock con datos completos (nombre, stock y umbral). */
    public StockResponse crearOActualizar(StockRequest request) {
        StockItem item = stockRepository.findByProductId(request.getProductId())
                .orElse(StockItem.builder().productId(request.getProductId()).build());

        item.setProductName(request.getProductName());
        item.setStock(request.getStock());
        item.setUmbralCritico(request.getUmbralCritico());

        return toResponse(stockRepository.save(item));
    }

    /** Ajusta stock en delta positivo/negativo, validando que no quede negativo. */
    public StockResponse ajustarStock(Long productId, int delta) {
        StockItem item = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Stock no encontrado para el producto"));
        int nuevoStock = item.getStock() + delta;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("Stock no puede ser negativo");
        }
        item.setStock(nuevoStock);
        return toResponse(stockRepository.save(item));
    }

    /** Lista stock por debajo o igual al umbral critico configurado. */
    public List<StockResponse> criticos() {
        return stockRepository.findAll().stream()
                .filter(it -> it.getStock() <= it.getUmbralCritico())
                .map(this::toResponse)
                .toList();
    }

    private StockResponse toResponse(StockItem item) {
        return StockResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .stock(item.getStock())
                .umbralCritico(item.getUmbralCritico())
                .critico(item.getStock() <= item.getUmbralCritico())
                .build();
    }
}
