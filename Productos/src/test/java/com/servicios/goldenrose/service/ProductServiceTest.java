package com.servicios.goldenrose.service;

import com.servicios.goldenrose.Model.Product;
import com.servicios.goldenrose.Repository.ProductRepository;
import com.servicios.goldenrose.dto.ProductRequest;
import com.servicios.goldenrose.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del servicio de productos (mapeos y filtros basicos). */
class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setup() {
        repository = mock(ProductRepository.class);
        service = new ProductService(repository);
    }

    @Test
    void crear_guarda_producto_con_datos_basicos() {
        ProductRequest request = new ProductRequest();
        request.setNombre("Vandal Prime");
        request.setPrecio(new BigDecimal("45.5"));
        request.setRareza("Legendary");
        request.setStock(10);

        when(repository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductResponse response = service.crear(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        Product saved = captor.getValue();

        assertThat(saved.getNombre()).isEqualTo("Vandal Prime");
        assertThat(saved.getPrecio()).isEqualByComparingTo("45.5");
        assertThat(saved.getStock()).isEqualTo(10);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Vandal Prime");
    }

    @Test
    void listarActivos_filtra_por_activo() {
        Product p = new Product();
        p.setId(3L);
        p.setNombre("Phantom Oni");
        p.setPrecio(new BigDecimal("30"));
        p.setActivo(true);

        when(repository.findByActivoTrue()).thenReturn(List.of(p));

        List<ProductResponse> list = service.listarActivos();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getNombre()).isEqualTo("Phantom Oni");
    }

}
