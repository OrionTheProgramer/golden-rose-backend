package com.servicios.goldenrose.service;

import com.servicios.goldenrose.Model.Product;
import com.servicios.goldenrose.Repository.ProductRepository;
import com.servicios.goldenrose.dto.ProductRequest;
import com.servicios.goldenrose.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/** Servicio de productos que expone CRUD y conversiones DTO. */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /** Lista solo productos activos. */
    public List<ProductResponse> listarActivos() {
        return productRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /** Obtiene producto por id, falla si no existe. */
    public ProductResponse obtener(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    /** Crea producto nuevo aplicando defaults. */
    public ProductResponse crear(ProductRequest request) {
        Product product = new Product();
        mapRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    /** Actualiza un producto existente. */
    public ProductResponse actualizar(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        mapRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    /** Marca como inactivo (soft delete). */
    public void eliminarLogico(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setActivo(false);
            productRepository.save(product);
        });
    }

    private void mapRequest(Product product, ProductRequest request) {
        product.setNombre(request.getNombre());
        product.setDescripcion(request.getDescripcion());
        product.setPrecio(request.getPrecio() != null ? request.getPrecio() : BigDecimal.ZERO);
        product.setRareza(request.getRareza());
        product.setImagenUrl(request.getImagenUrl());
        product.setStock(request.getStock());
        if (request.getActivo() != null) {
            product.setActivo(request.getActivo());
        }
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .descripcion(product.getDescripcion())
                .precio(product.getPrecio())
                .rareza(product.getRareza())
                .imagenUrl(product.getImagenUrl())
                .stock(product.getStock())
                .activo(product.isActivo())
                .creadoEn(product.getCreadoEn())
                .actualizadoEn(product.getActualizadoEn())
                .build();
    }
}
