package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Categoria;
import com.servicios.FoodExpress.Model.Producto;
import com.servicios.FoodExpress.Repository.CategoriaRepository;
import com.servicios.FoodExpress.Repository.ProductoRepository;
import com.servicios.FoodExpress.dto.ProductoRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoria = Categoria.builder().id(1L).nombre("Rifles").build();
    }

    @Test
    void crearProducto_ok() {
        ProductoRequest req = buildRequest();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(
                Producto.builder().id(10L).nombre("Vandal").precio(BigDecimal.valueOf(45)).categoria(categoria).build()
        );

        var resp = productoService.crear(req);

        assertEquals("Vandal", resp.getNombre());
        assertEquals(1L, resp.getCategoriaId());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void crearProducto_categoriaNoExiste() {
        ProductoRequest req = buildRequest();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productoService.crear(req));
    }

    @Test
    void eliminar_noExiste() {
        when(productoRepository.existsById(99L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> productoService.eliminar(99L));
    }

    private ProductoRequest buildRequest() {
        ProductoRequest req = new ProductoRequest();
        req.setNombre("Vandal");
        req.setPrecio(BigDecimal.valueOf(45));
        req.setCategoriaId(1L);
        req.setDescripcion("Skin de prueba");
        return req;
    }
}
