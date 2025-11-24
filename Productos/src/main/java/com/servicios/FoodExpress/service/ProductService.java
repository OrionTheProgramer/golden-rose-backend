package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Product;
import com.servicios.FoodExpress.Repository.ProductRepository;
import com.servicios.FoodExpress.dto.ProductRequest;
import com.servicios.FoodExpress.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient webClient;

    public List<ProductResponse> listarActivos() {
        return productRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse obtener(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    public ProductResponse crear(ProductRequest request, MultipartFile imagen) throws IOException {
        Product product = new Product();
        mapRequest(product, request, imagen);
        hydrateFromValorant(product, request.getReferenciaExterna());
        return toResponse(productRepository.save(product));
    }

    public ProductResponse actualizar(Long id, ProductRequest request, MultipartFile imagen) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        mapRequest(product, request, imagen);
        hydrateFromValorant(product, request.getReferenciaExterna());
        return toResponse(productRepository.save(product));
    }

    public void eliminarLogico(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setActivo(false);
            productRepository.save(product);
        });
    }

    public Optional<Product> obtenerEntidad(Long id) {
        return productRepository.findById(id);
    }

    private void mapRequest(Product product, ProductRequest request, MultipartFile imagen) throws IOException {
        product.setNombre(request.getNombre());
        product.setDescripcion(request.getDescripcion());
        product.setPrecio(Optional.ofNullable(request.getPrecio()).orElse(BigDecimal.ZERO));
        product.setRareza(request.getRareza());
        product.setCategoria(request.getCategoria());
        product.setReferenciaExterna(request.getReferenciaExterna());
        if (request.getActivo() != null) {
            product.setActivo(request.getActivo());
        }

        // Imagen remota URL si viene
        if (StringUtils.hasText(request.getImagenUrl())) {
            product.setImagenUrl(request.getImagenUrl());
        }

        // Imagen base64 (JSON)
        if (StringUtils.hasText(request.getImagenBase64())) {
            byte[] data = Base64.getDecoder().decode(request.getImagenBase64());
            product.setImagenData(data);
            product.setImagenContentType(
                    StringUtils.hasText(request.getImagenContentType())
                            ? request.getImagenContentType()
                            : MediaType.IMAGE_PNG_VALUE);
        }

        // Imagen multipart
        if (imagen != null && !imagen.isEmpty()) {
            product.setImagenData(imagen.getBytes());
            product.setImagenContentType(imagen.getContentType());
        }
    }

    private void hydrateFromValorant(Product product, String referenciaExterna) {
        if (!StringUtils.hasText(referenciaExterna)) {
            return;
        }
        try {
            ValorantSkinResponse.Skin skin = webClient.get()
                    .uri("https://valorant-api.com/v1/weapons/skinlevels/{uuid}", referenciaExterna)
                    .retrieve()
                    .bodyToMono(ValorantSkinResponse.class)
                    .onErrorResume(e -> Mono.empty())
                    .blockOptional()
                    .filter(ValorantSkinResponse::isOk)
                    .map(ValorantSkinResponse::getData)
                    .orElse(null);

            if (skin != null) {
                if (!StringUtils.hasText(product.getNombre())) {
                    product.setNombre(skin.displayName());
                }
                if (!StringUtils.hasText(product.getImagenUrl()) && StringUtils.hasText(skin.displayIcon())) {
                    product.setImagenUrl(skin.displayIcon());
                }
                if (!StringUtils.hasText(product.getRareza()) && StringUtils.hasText(skin.rarity())) {
                    product.setRareza(skin.rarity());
                }
            }
        } catch (Exception ignored) {
            // No romper flujo si Valorant API falla
        }
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .descripcion(product.getDescripcion())
                .precio(product.getPrecio())
                .rareza(product.getRareza())
                .categoria(product.getCategoria())
                .imagenUrl(product.getImagenUrl())
                .hasImageData(product.getImagenData() != null && product.getImagenData().length > 0)
                .imagenContentType(product.getImagenContentType())
                .activo(product.isActivo())
                .referenciaExterna(product.getReferenciaExterna())
                .creadoEn(product.getCreadoEn())
                .actualizadoEn(product.getActualizadoEn())
                .build();
    }

    /**
     * DTO parcial para Valorant API.
     */
    public record ValorantSkinResponse(boolean status, Skin data) {
        public boolean isOk() { return status; }
        public Skin getData() { return data; }
        public record Skin(String displayName, String displayIcon, String uuid, String rarity) {}
    }
}
