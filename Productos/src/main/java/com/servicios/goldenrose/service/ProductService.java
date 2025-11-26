package com.servicios.goldenrose.service;

import com.servicios.goldenrose.Model.Product;
import com.servicios.goldenrose.Repository.ProductRepository;
import com.servicios.goldenrose.dto.ProductRequest;
import com.servicios.goldenrose.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/** Servicio de productos que expone CRUD y conversiones DTO. */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private static final Map<String, String> RAREZA_ICON_MAP = Map.of(
            "ultra", "https://c-valorant-api.op.gg/Assets/ContentTiers/411E4A55-4E59-7757-41F0-86A53F101BB5.svg",
            "ultra edition", "https://c-valorant-api.op.gg/Assets/ContentTiers/411E4A55-4E59-7757-41F0-86A53F101BB5.svg",
            "exclusive", "https://c-valorant-api.op.gg/Assets/ContentTiers/E046854E-406C-37F4-6607-19A9BA8426FC.svg",
            "premium", "https://c-valorant-api.op.gg/Assets/ContentTiers/60BCA009-4182-7998-DEE7-B8A2558DC369.svg",
            "premiun", "https://c-valorant-api.op.gg/Assets/ContentTiers/60BCA009-4182-7998-DEE7-B8A2558DC369.svg",
            "deluxe", "https://c-valorant-api.op.gg/Assets/ContentTiers/0CEBB8BE-46D7-C12A-D306-E9907BFC5A25.svg",
            "select", "https://c-valorant-api.op.gg/Assets/ContentTiers/12683D76-48D7-84A3-4E09-6985794F0445.svg"
    );

    private final ProductRepository productRepository;
    private final ValorantSkinClient valorantSkinClient;

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
        autocompletarDesdeValorant(product);
        return toResponse(productRepository.save(product));
    }

    /** Crea producto permitiendo recibir imagen subida como archivo. */
    public ProductResponse crearConImagen(ProductRequest request, MultipartFile imagen) {
        Product product = new Product();
        mapRequest(product, request);
        aplicarImagenMultipart(product, imagen);
        autocompletarDesdeValorant(product);
        return toResponse(productRepository.save(product));
    }

    /** Actualiza un producto existente. */
    public ProductResponse actualizar(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        mapRequest(product, request);
        autocompletarDesdeValorant(product);
        return toResponse(productRepository.save(product));
    }

    /** Actualiza un producto y opcionalmente su imagen subida. */
    public ProductResponse actualizarConImagen(Long id, ProductRequest request, MultipartFile imagen) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        mapRequest(product, request);
        aplicarImagenMultipart(product, imagen);
        autocompletarDesdeValorant(product);
        return toResponse(productRepository.save(product));
    }

    /** Marca como inactivo (soft delete). */
    public void eliminarLogico(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setActivo(false);
            productRepository.save(product);
        });
    }

    public Optional<Product> buscarEntidad(Long id) {
        return productRepository.findById(id);
    }

    private void mapRequest(Product product, ProductRequest request) {
        product.setNombre(request.getNombre());
        product.setDescripcion(request.getDescripcion());
        product.setPrecio(request.getPrecio() != null ? request.getPrecio() : BigDecimal.ZERO);
        product.setRareza(request.getRareza());
        product.setReferenciaExterna(request.getReferenciaExterna());
        product.setImagenUrl(request.getImagenUrl());
        product.setStock(request.getStock());
        if (request.getActivo() != null) {
            product.setActivo(request.getActivo());
        }
        if (request.getImagenBase64() != null && !request.getImagenBase64().isBlank()) {
            byte[] data = java.util.Base64.getDecoder().decode(request.getImagenBase64());
            product.setImagenData(data);
            product.setImagenContentType(
                    request.getImagenContentType() != null ? request.getImagenContentType() : "image/png");
        }
    }

    private void aplicarImagenMultipart(Product product, MultipartFile imagen) {
        if (imagen != null && !imagen.isEmpty()) {
            try {
                product.setImagenData(imagen.getBytes());
                product.setImagenContentType(
                        imagen.getContentType() != null ? imagen.getContentType() : "image/png");
            } catch (Exception e) {
                throw new IllegalArgumentException("No se pudo leer la imagen cargada", e);
            }
        }
    }

    /**
     * Si no trae imagen propia, intenta buscar la skin en valorant-api y persistir la imagen + rareza.
     */
    private void autocompletarDesdeValorant(Product product) {
        boolean faltaImagen = (product.getImagenData() == null || product.getImagenData().length == 0)
                && (product.getImagenUrl() == null || product.getImagenUrl().isBlank());
        if (!faltaImagen && product.getRarezaIconUrl() != null) {
            return;
        }
        valorantSkinClient.findSkin(product.getNombre(), product.getReferenciaExterna())
                .ifPresent(skin -> {
                    if (faltaImagen) {
                        valorantSkinClient.downloadImage(skin.imageUrl())
                                .ifPresent(img -> {
                                    product.setImagenData(img.data());
                                    product.setImagenContentType(img.contentType());
                                    product.setImagenUrl(skin.imageUrl());
                                });
                    } else if (product.getImagenUrl() == null || product.getImagenUrl().isBlank()) {
                        product.setImagenUrl(skin.imageUrl());
                    }
                    if (product.getRareza() == null || product.getRareza().isBlank()) {
                        product.setRareza(skin.tierName());
                    }
                    product.setRarezaIconUrl(skin.tierIconUrl());
                });
        asignarIconoRareza(product);
    }

    private void asignarIconoRareza(Product product) {
        if (product.getRarezaIconUrl() != null && !product.getRarezaIconUrl().isBlank()) return;
        String rareza = product.getRareza();
        if (rareza == null || rareza.isBlank()) return;
        String key = rareza.trim().toLowerCase();
        if (RAREZA_ICON_MAP.containsKey(key)) {
            product.setRarezaIconUrl(RAREZA_ICON_MAP.get(key));
        }
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .descripcion(product.getDescripcion())
                .precio(product.getPrecio())
                .rareza(product.getRareza())
                .rarezaIconUrl(product.getRarezaIconUrl())
                .imagenUrl(product.getImagenUrl())
                .hasImageData(product.getImagenData() != null && product.getImagenData().length > 0)
                .referenciaExterna(product.getReferenciaExterna())
                .stock(product.getStock())
                .activo(product.isActivo())
                .creadoEn(product.getCreadoEn())
                .actualizadoEn(product.getActualizadoEn())
                .build();
    }
}
