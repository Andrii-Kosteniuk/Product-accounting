package dev.petproject.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Cascade;

import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

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
    @NotNull(message = "Product price can not be empty")
    private Double price;

    @NotNull
    @PositiveOrZero
    private int quantity;

    @Size(min = 5, message = "Description must be no less than 5 characters")
    @Size(max = 50, message = "Description must be no longer than 50 characters")
    private String description;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

}
