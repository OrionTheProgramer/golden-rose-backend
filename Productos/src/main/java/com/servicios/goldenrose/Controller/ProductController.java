package com.servicios.goldenrose.Controller;

import com.servicios.goldenrose.dto.ProductRequest;
import com.servicios.goldenrose.dto.ProductResponse;
import com.servicios.goldenrose.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** API REST principal para CRUD de productos. */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "CRUD de productos (nombre, descripcion, rareza, precio, imagenUrl, stock)")
public class ProductController {

    private final ProductService productService;

    /** Lista productos activos. */
    @GetMapping
    public CollectionModel<ProductResponse> listar() {
        List<ProductResponse> productos = productService.listarActivos().stream()
                .map(this::addLinks)
                .collect(Collectors.toList());
        return CollectionModel.of(productos)
                .add(linkTo(methodOn(ProductController.class).listar()).withSelfRel());
    }

    /** Obtiene producto por id. */
    @GetMapping("/{id}")
    public ProductResponse obtener(@PathVariable Long id) {
        return addLinks(productService.obtener(id));
    }

    @Operation(summary = "Crear producto (JSON simple)")
    @PostMapping
    public ResponseEntity<ProductResponse> crear(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = addLinks(productService.crear(request));
        return ResponseEntity.created(created.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(created);
    }

    @Operation(summary = "Crear producto con imagen (multipart/form-data)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> crearMultipart(
            @Valid @ModelAttribute ProductRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        ProductResponse created = addLinks(productService.crearConImagen(request, imagen));
        return ResponseEntity.created(created.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(created);
    }

    @Operation(summary = "Actualizar producto (JSON simple)")
    @PutMapping("/{id}")
    public ProductResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return addLinks(productService.actualizar(id, request));
    }

    @Operation(summary = "Actualizar producto con imagen (multipart/form-data)")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse actualizarMultipart(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        return addLinks(productService.actualizarConImagen(id, request, imagen));
    }

    /** Devuelve la imagen almacenada o redirige a la imagenUrl si existe. */
    @GetMapping("/{id}/imagen")
    public ResponseEntity<?> obtenerImagen(@PathVariable Long id) {
        return productService.buscarEntidad(id)
                .map(producto -> {
                    if (producto.getImagenData() != null && producto.getImagenData().length > 0) {
                        MediaType type = MediaType.parseMediaType(
                                producto.getImagenContentType() != null ? producto.getImagenContentType() : "image/png");
                        return ResponseEntity.ok()
                                .contentType(type)
                                .body(producto.getImagenData());
                    }
                    if (producto.getImagenUrl() != null) {
                        return ResponseEntity.status(HttpStatus.FOUND)
                                .header("Location", producto.getImagenUrl())
                                .body(null);
                    }
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /** Soft delete: marca como inactivo. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }

    /** Añade enlaces HATEOAS a la respuesta de producto. */
    private ProductResponse addLinks(ProductResponse response) {
        response.removeLinks();
        response.add(linkTo(methodOn(ProductController.class).obtener(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(ProductController.class).listar()).withRel("productos"));
        response.add(linkTo(methodOn(ProductController.class).obtenerImagen(response.getId())).withRel("imagen"));
        return response;
    }
}
