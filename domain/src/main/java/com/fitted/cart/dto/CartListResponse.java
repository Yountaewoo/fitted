package com.fitted.cart.dto;

import java.util.List;

public record CartListResponse(
        List<CartResponse> cartResponses
) {
}
