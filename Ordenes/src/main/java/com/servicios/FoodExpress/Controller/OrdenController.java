package com.servicios.FoodExpress.Controller;

import com.servicios.FoodExpress.Model.OrderStatus;
import com.servicios.FoodExpress.dto.ActualizarEstadoRequest;
import com.servicios.FoodExpress.dto.OrdenRequest;
import com.servicios.FoodExpress.dto.OrdenResponse;
import com.servicios.FoodExpress.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> listar() {
        return ResponseEntity.ok(ordenService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<OrdenResponse> crear(@Valid @RequestBody OrdenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenResponse> actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(ordenService.actualizarEstado(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        ordenService.eliminar(id);
    }
}
