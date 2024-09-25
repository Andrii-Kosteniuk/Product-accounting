package dev.petproject.repository;

import dev.petproject.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM products p WHERE p.name LIKE %:keyword%", nativeQuery = true)
    List<Product> findProductByKeyword(@Param(value = "keyword") String keyword);

}
