package com.fitted.product;

import com.fitted.loginUtils.LoginMemberId;
import com.fitted.product.dto.ProductListResponse;
import com.fitted.product.dto.ProductDetailResponse;
import com.fitted.product.dto.ProductRequest;
import org.springframework.data.domain.PageRequest;
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
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false) SortType sortType,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return productService.searchBy(name, category, sortType, pageRequest);
    }
}
