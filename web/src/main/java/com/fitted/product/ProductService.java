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

    private List<ProductOptionResponse> createProductOptions(ProductRequest request, Product product) {
        List<ProductOptionResponse> productOptionResponses = new ArrayList<>();
        for (ProductOptionRequest productOptionRequest : request.productOptionRequests()) {
            ProductOption productOption = productOptionRepository.save(
                    new ProductOption(
                            product.getId(),
                            productOptionRequest.size(),
                            productOptionRequest.productCount()));
            productOptionResponses.add(new ProductOptionResponse(
                    productOption.getId(),
                    productOption.getSize(),
                    productOption.getProductCount()));
        }
        return productOptionResponses;
    }

    private ProductDetailResponse assembleResponse(Product product, List<ProductOptionResponse> responses) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getDescription(),
                product.getImageUrl(),
                product.isActive(),
                responses
        );
    }


    @Transactional
    public ProductDetailResponse create(ProductRequest request) {
        Product product = createProductEntity(request);
        List<ProductOptionResponse> responses = createProductOptions(request, product);
        return assembleResponse(product, responses);
    }
}
