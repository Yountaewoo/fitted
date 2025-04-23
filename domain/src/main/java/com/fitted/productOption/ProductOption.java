package com.fitted.productOption;

import com.fitted.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    private int size;

    private int productCount;

    protected ProductOption() {
    }

    public ProductOption(Long productId, int size, int productCount) {
        this.productId = productId;
        this.size = size;
        this.productCount = productCount;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
