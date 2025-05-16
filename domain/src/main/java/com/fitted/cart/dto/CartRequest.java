package com.fitted.cart.dto;

public record CartRequest(
        Long productId,
        Long productOptionId,
        int productCount
) {
}
