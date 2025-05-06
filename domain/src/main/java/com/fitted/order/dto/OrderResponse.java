package com.fitted.order.dto;

import com.fitted.orderDetail.dto.OrderDetailResponse;

import java.util.List;

public record OrderResponse(
        Long orderId,
        String address,
        int totalPrice,
        List<OrderDetailResponse> responses
) {
}
