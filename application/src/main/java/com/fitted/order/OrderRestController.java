package com.fitted.order;

import com.fitted.loginUtils.LoginMemberId;
import com.fitted.order.dto.OrderRequest;
import com.fitted.order.dto.OrderResponse;
import com.fitted.order.dto.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public OrderResponse create(@LoginMemberId String supabaseId, @RequestBody OrderRequest request) {
        return orderService.create(supabaseId, request);
    }

    @GetMapping("/orders")
    public OrderListResponse getAll(@LoginMemberId String supabaseId) {
        return orderService.getAll(supabaseId);
    }

    @GetMapping("/orders/{orderId}")
    public OrderResponse findByOrderId(@LoginMemberId String supabaseId, @PathVariable Long orderId) {
        return orderService.findByOrderId(supabaseId, orderId);
    }

    @PutMapping("/orders/{orderId}")
    public void changeOrderStatus(@LoginMemberId String supabaseId, @PathVariable Long orderId) {
        orderService.changeOrderStatus(supabaseId, orderId);
    }
}
