package com.fitted.product.dto;

import java.util.List;

public record ProductDetailListResponse(
        List<ProductDetailResponse> responses
) {
}
