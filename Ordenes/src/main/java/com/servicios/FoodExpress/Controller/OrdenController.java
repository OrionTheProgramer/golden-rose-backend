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

/** API REST para crear y administrar ordenes. */
@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    /** Lista todas las ordenes. */
    @GetMapping
    public ResponseEntity<List<OrdenResponse>> listar() {
        return ResponseEntity.ok(ordenService.listar());
    }

    /** Obtiene una orden por id. */
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.obtener(id));
    }

    /** Crea nueva orden con items. */
    @PostMapping
    public ResponseEntity<OrdenResponse> crear(@Valid @RequestBody OrdenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(request));
    }

    /** Actualiza el estado de la orden. */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenResponse> actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(ordenService.actualizarEstado(id, request.getStatus()));
    }

    /** Elimina la orden. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        ordenService.eliminar(id);
    }
}
