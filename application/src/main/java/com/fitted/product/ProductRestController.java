package com.fitted.product;

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
    public ProductDetailResponse create(@RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @GetMapping("products/{productId}")
    public ProductDetailResponse findById(@PathVariable Long productId) {
        return productService.findById(productId);
    }
}
