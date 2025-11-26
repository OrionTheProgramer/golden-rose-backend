package com.servicios.FoodExpress.service;

import com.servicios.FoodExpress.Model.Categoria;
import com.servicios.FoodExpress.Repository.CategoriaRepository;
import com.servicios.FoodExpress.dto.CategoriaRequest;
import com.servicios.FoodExpress.dto.CategoriaResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Servicio CRUD de categorias con validacion de duplicados. */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    /** Lista todas las categorias disponibles. */
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    /** Obtiene una categoria por id o lanza excepcion si no existe. */
    public CategoriaResponse obtener(Long id) {
        return toResponse(getById(id));
    }

    /** Crea una categoria nueva validando nombre unico (case-insensitive). */
    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new IllegalArgumentException("La categoria ya existe");
        }
        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .build();
        return toResponse(categoriaRepository.save(categoria));
    }

    /** Actualiza una categoria y valida que el nuevo nombre no este duplicado. */
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = getById(id);
        if (!categoria.getNombre().equalsIgnoreCase(request.getNombre())
                && categoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new IllegalArgumentException("La categoria ya existe");
        }
        categoria.setNombre(request.getNombre());
        return toResponse(categoriaRepository.save(categoria));
    }

    /** Elimina categoria por id; si no existe lanza EntityNotFoundException. */
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoria no encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    private Categoria getById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada"));
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .build();
    }
}
