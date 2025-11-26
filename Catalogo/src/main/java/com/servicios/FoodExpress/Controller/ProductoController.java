package com.servicios.FoodExpress.Controller;

import com.servicios.FoodExpress.dto.ProductoRequest;
import com.servicios.FoodExpress.dto.ProductoResponse;
import com.servicios.FoodExpress.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

/**
 * API de productos del catalogo.
 * La escritura se redirige al microservicio Productos (8008) y este servicio conserva lectura/fallback.
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    @Value("${producto.service.url:http://localhost:8008}")
    private String productoServiceUrl;

    /** Lista todos los productos guardados localmente. */
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    /** Obtiene detalle de producto por id. */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtener(id));
    }

    /** Redirige la creacion al servicio Productos (JSON). */
    @PostMapping
    public ResponseEntity<Void> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(productoServiceUrl + "/api/productos"))
                .build();
    }

    /** Redirige la actualizacion al servicio Productos (JSON). */
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(productoServiceUrl + "/api/productos/" + id))
                .build();
    }

    /** Redirige creacion multipart (imagen subida) al servicio Productos. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> crearMultipart(
            @RequestPart("nombre") String nombre,
            @RequestPart("precio") String precio,
            @RequestPart("categoriaId") Long categoriaId,
            @RequestPart(value = "descripcion", required = false) String descripcion,
            @RequestPart(value = "imagenUrl", required = false) String imagenUrl,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(productoServiceUrl + "/api/productos"))
                .build();
    }

    /** Redirige actualizacion multipart al servicio Productos. */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> actualizarMultipart(
            @PathVariable Long id,
            @RequestPart("nombre") String nombre,
            @RequestPart("precio") String precio,
            @RequestPart("categoriaId") Long categoriaId,
            @RequestPart(value = "descripcion", required = false) String descripcion,
            @RequestPart(value = "imagenUrl", required = false) String imagenUrl,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(productoServiceUrl + "/api/productos/" + id))
                .build();
    }

    /** Elimina producto almacenado localmente (fallback). */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    /** Devuelve bytes de imagen almacenada o redirige a imagenUrl. */
    @GetMapping("/{id}/imagen")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
        var entidad = productoService.obtenerEntidad(id);
        if (entidad.getImagenData() != null && entidad.getImagenData().length > 0) {
            MediaType type = MediaType.parseMediaType(
                    entidad.getImagenContentType() != null ? entidad.getImagenContentType() : "image/png");
            return ResponseEntity.ok()
                    .contentType(type)
                    .body(entidad.getImagenData());
        }
        if (entidad.getImagenUrl() != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, entidad.getImagenUrl())
                    .build();
        }
        return ResponseEntity.notFound().build();
    }
}
