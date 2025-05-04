package com.fitted.productOption;

import com.fitted.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Entity
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    private int size;

    private int stock;

    public ProductOption(Long productId, int size, int stock) {
        this.productId = productId;
        this.size = size;
        this.stock = stock;
    }
}
