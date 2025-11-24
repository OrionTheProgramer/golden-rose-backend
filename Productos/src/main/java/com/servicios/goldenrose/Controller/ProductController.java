package com.servicios.goldenrose.Controller;

import com.servicios.goldenrose.dto.ProductRequest;
import com.servicios.goldenrose.dto.ProductResponse;
import com.servicios.goldenrose.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "CRUD de productos (nombre, descripcion, rareza, precio, imagenUrl, stock)")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> listar() {
        return productService.listarActivos();
    }

    @GetMapping("/{id}")
    public ProductResponse obtener(@PathVariable Long id) {
        return productService.obtener(id);
    }

    @Operation(summary = "Crear producto (JSON simple)")
    @PostMapping
    public ResponseEntity<ProductResponse> crear(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar producto (JSON simple)")
    @PutMapping("/{id}")
    public ProductResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}
