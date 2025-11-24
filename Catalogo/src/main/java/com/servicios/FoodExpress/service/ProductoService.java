package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Categoria;
import com.servicios.FoodExpress.Model.Producto;
import com.servicios.FoodExpress.Repository.CategoriaRepository;
import com.servicios.FoodExpress.Repository.ProductoRepository;
import com.servicios.FoodExpress.dto.ProductoRequest;
import com.servicios.FoodExpress.dto.ProductoResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductoResponse obtener(Long id) {
        return toResponse(getById(id));
    }

    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = getCategoria(request.getCategoriaId());
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .imagenUrl(request.getImagenUrl())
                .descripcion(request.getDescripcion())
                .categoria(categoria)
                .build();
        aplicarImagen(request, producto);
        return toResponse(productoRepository.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = getById(id);
        Categoria categoria = getCategoria(request.getCategoriaId());
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(categoria);
        aplicarImagen(request, producto);
        return toResponse(productoRepository.save(producto));
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    private Producto getById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    public Producto obtenerEntidad(Long id) {
        return getById(id);
    }

    private Categoria getCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
    }

    private ProductoResponse toResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .imagenUrl(producto.getImagenUrl())
                .hasImageData(producto.getImagenData() != null && producto.getImagenData().length > 0)
                .descripcion(producto.getDescripcion())
                .categoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null)
                .categoriaNombre(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
                .build();
    }

    private void aplicarImagen(ProductoRequest request, Producto producto) {
        if (request.getImagenBase64() != null && !request.getImagenBase64().isBlank()) {
            producto.setImagenData(Base64.getDecoder().decode(request.getImagenBase64()));
            producto.setImagenContentType(request.getImagenContentType() != null ? request.getImagenContentType() : "image/png");
        }
    }
}
