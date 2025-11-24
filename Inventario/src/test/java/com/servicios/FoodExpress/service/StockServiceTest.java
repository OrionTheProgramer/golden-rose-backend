package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.StockItem;
import com.servicios.FoodExpress.Repository.StockRepository;
import com.servicios.FoodExpress.dto.StockRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearOActualizar_creaNuevo() {
        StockRequest req = buildRequest();
        when(stockRepository.findByProductId(1L)).thenReturn(Optional.empty());
        when(stockRepository.save(any(StockItem.class))).thenReturn(
                StockItem.builder().id(5L).productId(1L).productName("Vandal").stock(10).umbralCritico(3).build()
        );

        var resp = stockService.crearOActualizar(req);

        assertEquals(1L, resp.getProductId());
        assertEquals(10, resp.getStock());
        verify(stockRepository).save(any(StockItem.class));
    }

    @Test
    void ajustarStock_ok() {
        StockItem item = StockItem.builder().productId(1L).productName("Vandal").stock(5).umbralCritico(2).build();
        when(stockRepository.findByProductId(1L)).thenReturn(Optional.of(item));
        when(stockRepository.save(any(StockItem.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = stockService.ajustarStock(1L, -2);
        assertEquals(3, resp.getStock());
    }

    @Test
    void ajustarStock_negativo() {
        StockItem item = StockItem.builder().productId(1L).productName("Vandal").stock(1).umbralCritico(0).build();
        when(stockRepository.findByProductId(1L)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> stockService.ajustarStock(1L, -5));
    }

    @Test
    void obtener_noExiste() {
        when(stockRepository.findByProductId(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stockService.obtenerPorProducto(99L));
    }

    private StockRequest buildRequest() {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setProductName("Vandal");
        req.setStock(10);
        req.setUmbralCritico(3);
        return req;
    }
}
