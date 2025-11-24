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

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    @Value("${producto.service.url:http://localhost:8008}")
    private String productoServiceUrl;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<Void> crear(@Valid @RequestBody ProductoRequest request) {
        // Redirige la creación al microservicio dedicado de Productos (8008) para evitar choques.
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(productoServiceUrl + "/api/productos"))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(productoServiceUrl + "/api/productos/" + id))
                .build();
    }

    // Crear con archivo (multipart)
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

    // Actualizar con archivo (multipart)
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

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
