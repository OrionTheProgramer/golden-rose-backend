package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Product;
import com.servicios.FoodExpress.Repository.ProductRepository;
import com.servicios.FoodExpress.dto.ProductRequest;
import com.servicios.FoodExpress.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setup() {
        repository = mock(ProductRepository.class);
        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.empty())
                .build();
        service = new ProductService(repository, webClient);
    }

    @Test
    void crear_guarda_producto_con_datos_basicos() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setNombre("Vandal Prime");
        request.setPrecio(new BigDecimal("45.5"));
        request.setRareza("Legendary");

        when(repository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductResponse response = service.crear(request, null);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        Product saved = captor.getValue();

        assertThat(saved.getNombre()).isEqualTo("Vandal Prime");
        assertThat(saved.getPrecio()).isEqualByComparingTo("45.5");
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

    @Test
    void obtenerEntidad_devuelve_optional() {
        Product p = new Product();
        p.setId(5L);
        when(repository.findById(5L)).thenReturn(Optional.of(p));

        Optional<Product> result = service.obtenerEntidad(5L);
        assertThat(result).isPresent();
    }
}
