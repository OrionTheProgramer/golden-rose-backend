package com.servicios.FoodExpress.Controller;

import com.servicios.FoodExpress.dto.StockRequest;
import com.servicios.FoodExpress.dto.StockResponse;
import com.servicios.FoodExpress.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockResponse>> listar() {
        return ResponseEntity.ok(stockService.listar());
    }

    @GetMapping("/producto/{productId}")
    public ResponseEntity<StockResponse> obtenerPorProducto(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.obtenerPorProducto(productId));
    }

    @GetMapping("/criticos")
    public ResponseEntity<List<StockResponse>> criticos() {
        return ResponseEntity.ok(stockService.criticos());
    }

    @PostMapping
    public ResponseEntity<StockResponse> crearOActualizar(@Valid @RequestBody StockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.crearOActualizar(request));
    }

    @PatchMapping("/producto/{productId}/ajustar")
    public ResponseEntity<StockResponse> ajustar(@PathVariable Long productId, @RequestParam int delta) {
        return ResponseEntity.ok(stockService.ajustarStock(productId, delta));
    }
}
