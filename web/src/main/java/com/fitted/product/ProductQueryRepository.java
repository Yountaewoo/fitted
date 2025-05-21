package com.fitted.product;

import com.fitted.productOption.QProductOption;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Pageable;
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

    List<Product> findAll(String productName, String category, SortType sortType, Pageable pageable) {
        OrderSpecifier<?> order = getOrderSpecifier(sortType);
        return queryFactory
                .selectFrom(product)
                .where(
                        findByCategory(category),
                        findByProductName(productName)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order)
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

    private OrderSpecifier<?> getOrderSpecifier(SortType sortType) {
        if (sortType == null) {
            return product.createdAt.asc();
        }

        switch (sortType) {
            case PRICE_ASC:
                return product.price.asc();
            case PRICE_DESC:
                return product.price.desc();
            case CREATED_ASC:
                return product.createdAt.asc();   // 오래된 순
            case CREATED_DESC:
                return product.createdAt.desc();  // 최신 순
            default:
                // 안전장치(여기에 도달할 일은 없지만)
                return product.createdAt.asc();
        }
    }

    public Long totalCount(String productName, String category) {
        Long totalCount = queryFactory.select(product.count())
                .from(product)
                .where(
                        findByCategory(category),
                        findByProductName(productName)
                )
                .fetchOne();
        return totalCount != null ? totalCount : 0L;
    }
}