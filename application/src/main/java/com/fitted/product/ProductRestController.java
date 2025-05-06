package com.fitted.product;

import com.fitted.loginUtils.LoginMemberId;
import com.fitted.product.dto.ProductListResponse;
import com.fitted.product.dto.ProductDetailResponse;
import com.fitted.product.dto.ProductRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ProductDetailResponse create(@LoginMemberId String supabaseId,
                                        @RequestBody ProductRequest request) {
        return productService.create(supabaseId, request);
    }

    @GetMapping("/products/{productId}")
    public ProductDetailResponse findById(@PathVariable Long productId) {
        return productService.findById(productId);
    }

    @DeleteMapping("/products/{productId}")
    public void deleteById(@LoginMemberId String supabaseId,
                           @PathVariable Long productId) {
        productService.deleteById(supabaseId, productId);
    }

    @GetMapping("/products")
    public ProductListResponse getAll(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String category) {
        return productService.searchBy(name, category);
    }
}
