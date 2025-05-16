package com.fitted.cart.dto;

import com.fitted.cartItem.dto.CartItemResponse;

public record CreateCartResponse(
        Long cartId,
        CartItemResponse cartItemResponse
) {
}
