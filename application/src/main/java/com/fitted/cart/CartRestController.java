package com.fitted.cart;

import com.fitted.cart.dto.CartListResponse;
import com.fitted.cart.dto.CartRequest;
import com.fitted.cart.dto.CartResponse;
import com.fitted.cart.dto.CreateCartResponse;
import com.fitted.loginUtils.LoginMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CartRestController {

    private final CartService cartService;

    @PostMapping("/carts")
    public CreateCartResponse create(@LoginMemberId String supabaseId, @RequestBody CartRequest request) {
        return cartService.create(supabaseId, request);
    }

    @GetMapping("/carts/{cartId}")
    public CartResponse findByCartId(@LoginMemberId String supabaseId, @PathVariable Long cartId) {
        return cartService.findByCartId(supabaseId, cartId);
    }

    @GetMapping("/carts")
    public CartListResponse findAll(@LoginMemberId String supabaseId) {
        return cartService.findAll(supabaseId);
    }
}
