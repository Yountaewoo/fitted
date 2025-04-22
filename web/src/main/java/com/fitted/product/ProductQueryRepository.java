package com.fitted.product;

import com.fitted.productOption.QProductOption;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;
    private final QProductOption productOption = QProductOption.productOption;

    public ProductQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    List<Product> findAll(String productName, String category) {
        return queryFactory
                .selectFrom(product)
                .where(
                        findByCategory(category),
                        findByProductName(productName)
                )
                .fetch();
    }

    private BooleanExpression findByProductName(String productName) {
        if (productName == null) {
            return null;
        }
        return product.name.contains(productName);
    }

    private BooleanExpression findByCategory(String categoryName) {
        if (categoryName == null) {
            return null;
        }
        try {
            Category category = Category.valueOf(categoryName.toUpperCase());
            return product.category.eq(category);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}