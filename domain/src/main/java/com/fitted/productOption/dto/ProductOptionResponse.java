package com.fitted.productOption.dto;

public record ProductOptionResponse(
        Long productOptionId,
        int size,
        int productCount
) {
}
