package dev.petproject.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Integer id;

    @Column(nullable = false)
    @NotEmpty(message = "Product name can not be empty")
    private String name;

    @PositiveOrZero(message = "Price can not be negative number")
    @NotNull(message = "Product price can not be empty or null")
    private Double price;

    @NotNull(message = "Quantity can not be empty or null")
    @PositiveOrZero(message = "Quantity can not be a negative")
    private int quantity;

    private String description;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

}
