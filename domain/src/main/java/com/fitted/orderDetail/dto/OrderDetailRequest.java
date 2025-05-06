package com.fitted.orderDetail.dto;

public record OrderDetailRequest(
        Long productOptionId,
        int productCount
) {
}
