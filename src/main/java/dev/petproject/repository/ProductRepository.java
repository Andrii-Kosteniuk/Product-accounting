package dev.petproject.repository;

import dev.petproject.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM products p WHERE p.name LIKE %:keyword%", nativeQuery = true)
    List<Product> findProductByKeyword(@Param(value = "keyword") String keyword);

    boolean existsByName(String name);

    @Modifying
    @Query("UPDATE Product p SET p.quantity = :newQuantity WHERE p.id = :id")
    void setNewAmount(@Param(value = "id") Integer id, @Param(value = "newQuantity") Integer amount);

    Optional<Product> findByName(String name);
}
