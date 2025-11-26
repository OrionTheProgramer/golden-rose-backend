package com.servicios.goldenrose.Repository;

import com.servicios.goldenrose.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repositorio JPA de productos con helper para filtrar activos. */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActivoTrue();
}
