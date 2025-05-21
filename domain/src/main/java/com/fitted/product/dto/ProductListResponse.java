package com.fitted.product.dto;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> responses,
        Long  totalCount
) {
}
