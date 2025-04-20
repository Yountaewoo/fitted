package com.fitted.product;

import com.fitted.product.dto.ProductDetailResponse;
import com.fitted.product.dto.ProductRequest;
import com.fitted.productOption.ProductOption;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.productOption.dto.ProductOptionRequest;
import com.fitted.productOption.dto.ProductOptionResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public ProductService(ProductRepository productRepository, ProductOptionRepository productOptionRepository) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    private Product createProductEntity(ProductRequest request) {
        return productRepository.save(new Product(
                request.name(),
                request.price(),
                request.category(),
                request.description(),
                request.imageUrl()));
    }

    private List<ProductOption> createProductOptions(ProductRequest request, Product product) {
        return request.productOptionRequests().stream()
                .map(productOptionRequest -> new ProductOption(
                        product.getId(),
                        productOptionRequest.size(),
                        productOptionRequest.productCount()))
                .toList();
    }

    private ProductDetailResponse assembleResponse(Product product, List<ProductOption> productOptionList) {
        List<ProductOptionResponse> productOptionResponses = productOptionList.stream()
                .map(productOption -> new ProductOptionResponse(
                        productOption.getId(),
                        productOption.getSize(),
                        productOption.getProductCount()))
                .toList();
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getDescription(),
                product.getImageUrl(),
                product.isActive(),
                productOptionResponses
        );
    }

    @Transactional
    public ProductDetailResponse create(ProductRequest request) {
        Product product = createProductEntity(request);
        List<ProductOption> productOptions = productOptionRepository.saveAll(createProductOptions(request, product));
        return assembleResponse(product, productOptions);
    }

    public ProductDetailResponse findById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NoSuchElementException("해당하는 상품이 없습니다."));
        List<ProductOption> productOptionList = productOptionRepository.findAllByProductId(productId);
        return assembleResponse(product, productOptionList);
    }
}
