package com.fitted.cartItem.dto;

public record CartItemResponse(
        Long cartItemId,
        Long productId,
        Long productOptionId,
        int productCount
) {
}
