package com.fitted.product.dto;

import com.fitted.product.Category;

public record ProductResponse(
        Long productId,
        String productName,
        int price,
        Category category,
        String description,
        String imageUrl,
        boolean active
) {
}
