package com.fitted.order.dto;

import java.util.List;

public record OrderListResponse(
        List<OrderSummaryResponse> orderSummaryResponses
) {
}
