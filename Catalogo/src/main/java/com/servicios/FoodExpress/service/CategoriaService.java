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

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CategoriaResponse obtener(Long id) {
        return toResponse(getById(id));
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new IllegalArgumentException("La categoría ya existe");
        }
        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .build();
        return toResponse(categoriaRepository.save(categoria));
    }

    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = getById(id);
        if (!categoria.getNombre().equalsIgnoreCase(request.getNombre())
                && categoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new IllegalArgumentException("La categoría ya existe");
        }
        categoria.setNombre(request.getNombre());
        return toResponse(categoriaRepository.save(categoria));
    }

    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    private Categoria getById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .build();
    }
}
