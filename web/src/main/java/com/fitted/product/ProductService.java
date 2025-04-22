package com.fitted.product;

import com.fitted.product.dto.ProductListResponse;
import com.fitted.product.dto.ProductDetailResponse;
import com.fitted.product.dto.ProductRequest;
import com.fitted.product.dto.ProductResponse;
import com.fitted.productOption.ProductOption;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.productOption.dto.ProductOptionResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductQueryRepository productQueryRepository;

    public ProductService(ProductRepository productRepository, ProductOptionRepository productOptionRepository, ProductQueryRepository productQueryRepository) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
        this.productQueryRepository = productQueryRepository;
    }

    private Product createProductEntity(ProductRequest request) {
        return new Product(
                request.name(),
                request.price(),
                request.category(),
                request.description(),
                request.imageUrl());
    }

    private List<ProductOption> createProductOptionsEntity(ProductRequest request, Product product) {
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

    private ProductListResponse fetchProductListResponse(List<Product> products) {
        return new ProductListResponse(products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getCategory(),
                        product.getDescription(),
                        product.getImageUrl(),
                        product.isActive()))
                .toList());
    }

    @Transactional
    public ProductDetailResponse create(ProductRequest request) {
        Product product = createProductEntity(request);
        Product saveProduct = productRepository.save(product);
        List<ProductOption> productOptions = createProductOptionsEntity(request, product);
        List<ProductOption> saveProductOptions = productOptionRepository.saveAll(productOptions);
        return assembleResponse(saveProduct, saveProductOptions);
    }

    public ProductDetailResponse findById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NoSuchElementException("해당하는 상품이 없습니다."));
        List<ProductOption> productOptionList = productOptionRepository.findAllByProductId(productId);
        return assembleResponse(product, productOptionList);
    }

    @Transactional
    public void deleteById(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NoSuchElementException("해당하는 상품이 없습니다.");
        }
        productOptionRepository.deleteAllByProductId(productId);
        productRepository.deleteById(productId);
    }

    public ProductListResponse searchBy(String name, String category) {
        List<Product> products = productQueryRepository.findAll(name, category);
        return fetchProductListResponse(products);
    }
}
