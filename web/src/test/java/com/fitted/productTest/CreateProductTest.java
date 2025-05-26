package com.fitted.productTest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.fitted.product.Category;
import com.fitted.product.Product;
import com.fitted.product.ProductRepository;
import com.fitted.product.ProductService;
import com.fitted.product.dto.ProductDetailResponse;
import com.fitted.product.dto.ProductRequest;
import com.fitted.productOption.ProductOption;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.productOption.dto.ProductOptionRequest;
import com.fitted.productOption.dto.ProductOptionResponse;
import com.fitted.security.AuthorizationService;
import com.fitted.user.User;
import com.fitted.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CreateProductTest {

    @Mock
    AuthorizationService authorizationService;
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductOptionRepository productOptionRepository;

    @InjectMocks
    ProductService productService;

    private ProductRequest request;
    private List<ProductOptionRequest> optionReqs;

    @BeforeEach
    void setUp() {
        optionReqs = List.of(
                new ProductOptionRequest(90, 2),
                new ProductOptionRequest(100, 5)
        );
        request = new ProductRequest(
                "TestProduct",
                15000,
                Category.JEANS,
                "A nice pair of jeans",
                "http://img.test/jeans.jpg",
                optionReqs
        );
    }

    @Test
    void create_WhenAdmin_SavesAndReturnsExpectedResponse() {
        // given
        String adminId = "admin-123";
        // User 생성: supabaseId, password, email, name
        User adminUser = new User("admin-123", "pass", "admin@test.com", "Admin");
        // role 필드 직접 설정
        ReflectionTestUtils.setField(adminUser, "role", Role.ADMIN);
        given(authorizationService.checkAdmin(adminId))
                .willReturn(adminUser);

        // stub save Product
        Product savedProduct = new Product(
                request.name(),
                request.price(),
                request.category(),
                request.description(),
                request.imageUrl()
        );
        ReflectionTestUtils.setField(savedProduct, "id", 42L);
        given(productRepository.save(any(Product.class)))
                .willReturn(savedProduct);

        // stub saveAll Options
        ProductOption opt1 = new ProductOption(42L, 90, 2);
        ProductOption opt2 = new ProductOption(42L, 100, 5);
        ReflectionTestUtils.setField(opt1, "id", 100L);
        ReflectionTestUtils.setField(opt2, "id", 101L);
        given(productOptionRepository.saveAll(anyList()))
                .willReturn(List.of(opt1, opt2));

        // when
        ProductDetailResponse response = productService.create(adminId, request);

        // then
        then(authorizationService).should().checkAdmin(adminId);
        then(productRepository).should().save(any(Product.class));
        then(productOptionRepository).should().saveAll(anyList());

        assertThat(response.productId()).isEqualTo(42L);
        assertThat(response.productName()).isEqualTo("TestProduct");
        assertThat(response.price()).isEqualTo(15000);
        assertThat(response.category()).isEqualTo(Category.JEANS);
        assertThat(response.description()).isEqualTo("A nice pair of jeans");
        assertThat(response.imageUrl()).isEqualTo("http://img.test/jeans.jpg");
        assertThat(response.active()).isTrue();

        List<ProductOptionResponse> opts = response.productOptionResponses();
        assertThat(opts).hasSize(2)
                .extracting(ProductOptionResponse::size, ProductOptionResponse::productCount, ProductOptionResponse::productOptionId)
                .containsExactlyInAnyOrder(
                        tuple(90, 2, 100L),
                        tuple(100, 5, 101L)
                );
    }

    @Test
    void create_WhenNotAdmin_ThrowsIllegalState() {
        // given
        String userId = "user-999";
        given(authorizationService.checkAdmin(userId))
                .willThrow(new IllegalStateException("관리자가 아닙니다."));

        // then
        assertThatThrownBy(() -> productService.create(userId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("관리자가 아닙니다.");

        then(productRepository).should(never()).save(any());
        then(productOptionRepository).should(never()).saveAll(any());
    }
}
