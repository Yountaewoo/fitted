package com.fitted.product;

import com.fitted.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int price;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    private String description;

    private String imageUrl;

    private boolean active = true;

    protected Product() {
    }

    public Product(String name, int price, Category category, String description, String imageUrl) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
