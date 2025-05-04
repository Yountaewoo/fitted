package com.fitted.order;

import com.fitted.loginUtils.LoginMemberId;
import com.fitted.order.dto.OrderRequest;
import com.fitted.order.dto.OrderResponse;
import com.fitted.order.dto.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public OrderResponse create(@LoginMemberId String supabaseId, @RequestBody OrderRequest request) {
        return orderService.create(supabaseId, request);
    }

    @GetMapping("orders")
    public OrderListResponse getAll(@LoginMemberId String supabaseId) {
        return orderService.getAll(supabaseId);
    }
}
