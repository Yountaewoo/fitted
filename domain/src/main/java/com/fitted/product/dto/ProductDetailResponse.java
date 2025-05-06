package com.fitted.product.dto;

import com.fitted.product.Category;
import com.fitted.productOption.dto.ProductOptionRequest;
import com.fitted.productOption.dto.ProductOptionResponse;

import java.util.List;

public record ProductDetailResponse(
        Long productId,
        String productName,
        int price,
        Category category,
        String description,
        String imageUrl,
        boolean active,
        List<ProductOptionResponse> productOptionResponses
) {
}
