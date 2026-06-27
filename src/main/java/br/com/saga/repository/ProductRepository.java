package br.com.saga.repository;

import br.com.saga.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByProductCategoryName(String categoryName, Pageable pageable);
}
