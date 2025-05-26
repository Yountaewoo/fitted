package com.fitted.productTest;

import com.fitted.product.ProductService;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.product.ProductRepository;
import com.fitted.security.AuthorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private ProductService productService;

    @Test
    void deleteById_WhenProductExists_DeletesOptionsAndProduct() {
        // Arrange
        String supabaseId = "admin-id";
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);

        // Act
        productService.deleteById(supabaseId, productId);

        // Assert
        verify(authorizationService).checkAdmin(supabaseId);
        verify(productOptionRepository).deleteAllByProductId(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void deleteById_WhenProductNotExists_ThrowsNoSuchElementException() {
        // Arrange
        String supabaseId = "admin-id";
        Long productId = 2L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> productService.deleteById(supabaseId, productId));
        assertEquals("해당하는 상품이 없습니다.", exception.getMessage());

        verify(authorizationService).checkAdmin(supabaseId);
        verify(productOptionRepository, never()).deleteAllByProductId(anyLong());
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteById_WhenUserNotAdmin_ThrowsIllegalStateException() {
        // Arrange
        String supabaseId = "non-admin";
        Long productId = 3L;
        doThrow(new IllegalStateException("관리자가 아닙니다.")).when(authorizationService).checkAdmin(supabaseId);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> productService.deleteById(supabaseId, productId));
        assertEquals("관리자가 아닙니다.", exception.getMessage());

        verify(authorizationService).checkAdmin(supabaseId);
        verify(productRepository, never()).existsById(anyLong());
        verify(productOptionRepository, never()).deleteAllByProductId(anyLong());
        verify(productRepository, never()).deleteById(anyLong());
    }
}
