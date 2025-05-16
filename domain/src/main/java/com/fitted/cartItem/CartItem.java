package com.fitted.cartItem;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long productOptionId;

    @Column(nullable = false)
    private int productCount;

    @Column(nullable = false)
    private Long cartId;

    public CartItem(Long productId, Long productOptionId, int productCount, Long cartId) {
        this.productId = productId;
        this.productOptionId = productOptionId;
        this.productCount = productCount;
        this.cartId = cartId;
    }
}
