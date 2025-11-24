package com.servicios.FoodExpress.Controller;

import com.servicios.FoodExpress.dto.ProductRequest;
import com.servicios.FoodExpress.dto.ProductResponse;
import com.servicios.FoodExpress.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "CRUD de skins/productos Valorant")
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

    @GetMapping("/{id}/imagen")
    public ResponseEntity<?> obtenerImagen(@PathVariable Long id) {
        return productService.obtenerEntidad(id)
                .map(prod -> {
                    if (prod.getImagenData() != null && prod.getImagenData().length > 0) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.parseMediaType(
                                StringUtils.hasText(prod.getImagenContentType())
                                        ? prod.getImagenContentType()
                                        : MediaType.IMAGE_PNG_VALUE));
                        return new ResponseEntity<>(prod.getImagenData(), headers, HttpStatus.OK);
                    }
                    if (StringUtils.hasText(prod.getImagenUrl())) {
                        return ResponseEntity.status(HttpStatus.FOUND)
                                .location(URI.create(prod.getImagenUrl()))
                                .build();
                    }
                    return ResponseEntity.notFound().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Crear producto (multipart)",
            description = "Permite subir imagen binaria y campos de producto. Postman mostrará los campos del formulario automáticamente.",
            requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, examples = {
                    @ExampleObject(name = "Producto multipart",
                            value = """
                                    --form-data--
                                    nombre=Vandal Champions 2025
                                    precio=49.99
                                    categoria=Rifle
                                    rareza=Exclusive
                                    descripcion=Edición Champions 2025 con animaciones
                                    referenciaExterna=placeholder-champions-2025
                                    imagen=@champions.png
                                    """)
            }))
    )
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductResponse> crearMultipart(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "rareza", required = false) String rareza,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "imagenUrl", required = false) String imagenUrl,
            @RequestParam(value = "activo", required = false) Boolean activo,
            @RequestParam(value = "referenciaExterna", required = false) String referenciaExterna,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        ProductRequest request = new ProductRequest();
        request.setNombre(nombre);
        request.setPrecio(BigDecimal.valueOf(precio));
        request.setDescripcion(descripcion);
        request.setRareza(rareza);
        request.setCategoria(categoria);
        request.setImagenUrl(imagenUrl);
        request.setActivo(activo);
        request.setReferenciaExterna(referenciaExterna);

        ProductResponse created = productService.crear(request, imagen);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Crear producto (JSON/base64 o URL)",
            requestBody = @RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductRequest.class), examples = {
                    @ExampleObject(name = "Producto JSON con base64",
                            value = """
                                    {
                                      "nombre": "Phantom Oni",
                                      "precio": 39.9,
                                      "descripcion": "Edición limitada",
                                      "rareza": "Exclusive",
                                      "categoria": "Rifle",
                                      "imagenBase64": "<base64>",
                                      "imagenContentType": "image/png",
                                      "referenciaExterna": "f048e0fa-4de4-2729-21e6-bad2a1421d00"
                                    }
                                    """)
            }))
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> crearJson(@Valid @RequestBody ProductRequest request) throws IOException {
        ProductResponse created = productService.crear(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ProductResponse actualizarMultipart(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "rareza", required = false) String rareza,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "imagenUrl", required = false) String imagenUrl,
            @RequestParam(value = "activo", required = false) Boolean activo,
            @RequestParam(value = "referenciaExterna", required = false) String referenciaExterna,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        ProductRequest request = new ProductRequest();
        request.setNombre(nombre);
        request.setPrecio(BigDecimal.valueOf(precio));
        request.setDescripcion(descripcion);
        request.setRareza(rareza);
        request.setCategoria(categoria);
        request.setImagenUrl(imagenUrl);
        request.setActivo(activo);
        request.setReferenciaExterna(referenciaExterna);

        return productService.actualizar(id, request, imagen);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse actualizarJson(@PathVariable Long id, @Valid @RequestBody ProductRequest request) throws IOException {
        return productService.actualizar(id, request, null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}
