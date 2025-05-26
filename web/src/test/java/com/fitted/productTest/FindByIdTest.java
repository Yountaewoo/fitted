package com.fitted.productTest;

import com.fitted.product.Category;
import com.fitted.product.Product;
import com.fitted.product.ProductRepository;
import com.fitted.product.ProductService;
import com.fitted.product.dto.ProductDetailResponse;
import com.fitted.productOption.ProductOption;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.productOption.dto.ProductOptionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FindByIdTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;
    private List<ProductOption> sampleOptions;

    @BeforeEach
    void setUp() {
        // 1) id 없이 순수 생성자만 쓰고
        sampleProduct = new Product(
                "슬림핏 데님",
                55_000,
                Category.JEANS,
                "가볍고 편안한 슬림핏 데님 팬츠",
                "http://image.example.com/denim.jpg"
        );
        // 2) 리플렉션으로 private 필드 id 주입
        ReflectionTestUtils.setField(sampleProduct, "id", 1L);

        // 옵션도 같은 방식으로 id 없이 생성 → 리플렉션으로 id 채우기
        ProductOption opt1 = new ProductOption(1L, 90, 5);
        ReflectionTestUtils.setField(opt1, "id", 11L);

        ProductOption opt2 = new ProductOption(1L, 95, 3);
        ReflectionTestUtils.setField(opt2, "id", 12L);

        sampleOptions = List.of(opt1, opt2);
    }

    @Test
    void findById_WhenProductExists_ReturnsDetailResponse() {
        // given
        given(productRepository.findById(1L)).willReturn(Optional.of(sampleProduct));
        given(productOptionRepository.findAllByProductId(1L)).willReturn(sampleOptions);

        // when
        ProductDetailResponse response = productService.findById(1L);

        // then: 기본 필드
        assertThat(response.productId()).isEqualTo(1L);
        assertThat(response.productName()).isEqualTo("슬림핏 데님");
        assertThat(response.price()).isEqualTo(55_000);
        assertThat(response.category()).isEqualTo(Category.JEANS);
        assertThat(response.description()).isEqualTo("가볍고 편안한 슬림핏 데님 팬츠");
        assertThat(response.imageUrl()).endsWith("denim.jpg");
        assertThat(response.active()).isTrue();

        // 옵션 리스트: ID까지 검증
        assertThat(response.productOptionResponses())
                .hasSize(2)
                .extracting(
                        ProductOptionResponse::productOptionId,
                        ProductOptionResponse::size,
                        ProductOptionResponse::productCount
                )
                .containsExactly(
                        tuple(11L, 90, 5),
                        tuple(12L, 95, 3)
                );

        then(productRepository).should().findById(1L);
        then(productOptionRepository).should().findAllByProductId(1L);
    }

    @Test
    void findById_WhenProductNotFound_ThrowsNoSuchElementException() {
        // given
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당하는 상품이 없습니다.");

        then(productRepository).should().findById(999L);
        then(productOptionRepository).shouldHaveNoInteractions();
    }
}
