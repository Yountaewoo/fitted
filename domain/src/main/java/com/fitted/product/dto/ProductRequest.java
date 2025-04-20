package com.fitted.product.dto;

import com.fitted.product.Category;
import com.fitted.productOption.dto.ProductOptionRequest;

import java.util.List;

public record ProductRequest(
        String name,
        int price,
        Category category,
        String description,
        String imageUrl,
        List<ProductOptionRequest> productOptionRequests
) {
}
