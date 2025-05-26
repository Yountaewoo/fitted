package com.fitted.productTest;

import com.fitted.product.*;
import com.fitted.product.dto.ProductListResponse;
import com.fitted.product.dto.ProductResponse;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.security.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchByTest {

    @Mock
    private ProductQueryRepository productQueryRepository;

    // 실제 의존성은 사용되지 않으므로 Mockito가 stub 처리
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("검색 조건에 해당하는 상품이 없으면 빈 리스트와 0 반환")
    void searchBy_emptyResult() {
        // given
        String name = "nonexistent";
        String category = "NONE";
        SortType sortType = SortType.PRICE_ASC;
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(productQueryRepository.findAll(name, category, sortType, pageRequest))
                .thenReturn(Collections.emptyList());
        when(productQueryRepository.totalCount(name, category))
                .thenReturn(0L);

        // when
        ProductListResponse response = productService.searchBy(name, category, sortType, pageRequest);

        // then
        assertThat(response.responses()).isEmpty();
        assertThat(response.totalCount()).isZero();

        verify(productQueryRepository, times(1))
                .findAll(name, category, sortType, pageRequest);
        verify(productQueryRepository, times(1))
                .totalCount(name, category);
    }

    @Test
    @DisplayName("검색 조건에 해당하는 상품이 있으면 매핑된 DTO 리스트와 총 개수 반환")
    void searchBy_withResults() {
        // given
        String name = "T-Shirt";
        String category = Category.T_SHIRT.name();  // "T_SHIRT"
        SortType sortType = SortType.PRICE_DESC;
        PageRequest pageRequest = PageRequest.of(1, 5);

        // 실제 Product 엔티티 생성 (id는 null이지만 매핑 로직만 검증)
        Product p1 = new Product("T-Shirt", 20000, Category.T_SHIRT, "Nice shirt", "url1");
        Product p2 = new Product("T-Shirt Pro", 30000, Category.T_SHIRT, "Premium shirt", "url2");
        List<Product> products = List.of(p1, p2);

        when(productQueryRepository.findAll(name, category, sortType, pageRequest))
                .thenReturn(products);
        when(productQueryRepository.totalCount(name, category))
                .thenReturn(2L);

        // when
        ProductListResponse response = productService.searchBy(name, category, sortType, pageRequest);

        // then
        assertThat(response.totalCount()).isEqualTo(2L);

        List<ProductResponse> dtos = response.responses();
        assertThat(dtos).hasSize(2);

        // 첫 번째 DTO 필드 검증
        ProductResponse dto1 = dtos.get(0);
        assertThat(dto1.productName()).isEqualTo("T-Shirt");
        assertThat(dto1.price()).isEqualTo(20000);
        assertThat(dto1.category()).isEqualTo(Category.T_SHIRT);
        assertThat(dto1.description()).isEqualTo("Nice shirt");
        assertThat(dto1.imageUrl()).isEqualTo("url1");
        assertThat(dto1.active()).isTrue();

        // 두 번째 DTO 필드 검증
        ProductResponse dto2 = dtos.get(1);
        assertThat(dto2.productName()).isEqualTo("T-Shirt Pro");
        assertThat(dto2.price()).isEqualTo(30000);
        assertThat(dto2.category()).isEqualTo(Category.T_SHIRT);
        assertThat(dto2.description()).isEqualTo("Premium shirt");
        assertThat(dto2.imageUrl()).isEqualTo("url2");
        assertThat(dto2.active()).isTrue();

        verify(productQueryRepository, times(1))
                .findAll(name, category, sortType, pageRequest);
        verify(productQueryRepository, times(1))
                .totalCount(name, category);
    }
}
