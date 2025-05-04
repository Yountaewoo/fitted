package com.fitted.order.dto;

public record OrderSummaryResponse(
        Long orderId,
        String address,
        int totalPrice
) {}
