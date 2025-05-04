package com.fitted.order.dto;

import com.fitted.orderDetail.dto.OrderDetailRequest;

import java.util.List;

public record OrderRequest(
        List<OrderDetailRequest> requests,
        String address
) {
}
