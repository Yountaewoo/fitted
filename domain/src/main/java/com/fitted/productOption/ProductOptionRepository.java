package com.fitted.productOption;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllByProductId(Long productId);
    void deleteAllByProductId(Long productId);
}
