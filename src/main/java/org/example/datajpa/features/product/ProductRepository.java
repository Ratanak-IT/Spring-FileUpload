package org.example.datajpa.features.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Boolean existsBySlug (String slug);
    Boolean existsByCode(String code);
    Boolean existsByName(String name);
    Optional<Product> findByCode(String code);
}
