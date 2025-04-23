package com.fitted.product;

import com.fitted.product.dto.ProductDetailResponse;
import com.fitted.product.dto.ProductListResponse;
import com.fitted.product.dto.ProductRequest;
import com.fitted.product.dto.ProductResponse;
import com.fitted.productOption.ProductOption;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.productOption.dto.ProductOptionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductQueryRepository productQueryRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest sampleRequest;
    private Product sampleProduct;
    private List<ProductOption> sampleOptions;

    @BeforeEach
    void setUp() {
        // 공통 샘플 객체 준비
        sampleRequest = new ProductRequest(
                "T-Shirt", 25000, Category.T_SHIRT,
                "Comfortable cotton tee", "http://img.url",
                List.of(
                        new ProductOptionRequest(100, 10),
                        new ProductOptionRequest(105, 5)
                )
        );
        sampleProduct = new Product(
                "T-Shirt", 25000, Category.T_SHIRT,
                "Comfortable cotton tee", "http://img.url"
        );
        // id 세팅 (save 후 반환될 객체)
        sampleProduct.setId(1L);

        sampleOptions = List.of(
                new ProductOption(1L, 100, 10),
                new ProductOption(1L, 105, 5)
        );
        // 옵션에도 id 세팅
        sampleOptions.get(0).setId(100L);
        sampleOptions.get(1).setId(101L);
    }

    @Test
    void create_정상저장후_assembleResponse반환() {
        // given
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);
        when(productOptionRepository.saveAll(anyList())).thenReturn(sampleOptions);

        // when
        ProductDetailResponse response = productService.create(sampleRequest);

        // then
        assertThat(response.productId()).isEqualTo(1L);
        assertThat(response.productName()).isEqualTo("T-Shirt");

        verify(productRepository, times(1)).save(any(Product.class));
        verify(productOptionRepository, times(1)).saveAll(anyList());
    }


    @Test
    void findById_존재하는상품_조회성공() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productOptionRepository.findAllByProductId(1L)).thenReturn(sampleOptions);

        // when
        ProductDetailResponse resp = productService.findById(1L);

        // then
        assertThat(resp.productId()).isEqualTo(1L);
        assertThat(resp.productName()).isEqualTo("T-Shirt");
        assertThat(resp.productOptionResponses()).hasSize(2);

        verify(productRepository).findById(1L);
        verify(productOptionRepository).findAllByProductId(1L);
    }

    @Test
    void deleteById_존재하는상품_삭제로직호출() {
        // given
        when(productRepository.existsById(1L)).thenReturn(true);

        // when
        productService.deleteById(1L);

        // then
        verify(productOptionRepository).deleteAllByProductId(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteById_없는상품_예외발생() {
        // given
        when(productRepository.existsById(anyLong())).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> productService.deleteById(5L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당하는 상품이 없습니다.");

        verify(productRepository).existsById(5L);
        verify(productOptionRepository, never()).deleteAllByProductId(anyLong());
    }

    @Test
    void searchBy_쿼리실행후_ListResponse반환() {
        // given
        List<Product> products = List.of(sampleProduct);
        when(productQueryRepository.findAll("T-Shirt", "CLOTHING")).thenReturn(products);

        // when
        ProductListResponse listResp = productService.searchBy("T-Shirt", "CLOTHING");

        // then
        assertThat(listResp.responses())
                .hasSize(1)
                .extracting(ProductResponse::productName)
                .contains("T-Shirt");

        verify(productQueryRepository).findAll("T-Shirt", "CLOTHING");
    }
}

