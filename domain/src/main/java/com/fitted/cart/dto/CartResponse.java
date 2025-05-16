package com.fitted.cart.dto;

import com.fitted.cartItem.dto.CartItemResponse;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> cartItemResponses
) {
}
