package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Categoria;
import com.servicios.FoodExpress.Repository.CategoriaRepository;
import com.servicios.FoodExpress.dto.CategoriaRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearCategoria_ok() {
        CategoriaRequest req = new CategoriaRequest();
        req.setNombre("Rifles");

        when(categoriaRepository.existsByNombreIgnoreCase("Rifles")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(Categoria.builder().id(1L).nombre("Rifles").build());

        var resp = categoriaService.crear(req);

        assertEquals("Rifles", resp.getNombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void crearCategoria_duplicada() {
        CategoriaRequest req = new CategoriaRequest();
        req.setNombre("Rifles");

        when(categoriaRepository.existsByNombreIgnoreCase("Rifles")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoriaService.crear(req));
    }

    @Test
    void eliminar_noExiste() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> categoriaService.eliminar(99L));
    }
}
