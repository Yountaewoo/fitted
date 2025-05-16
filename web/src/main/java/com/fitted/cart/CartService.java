package com.fitted.cart;

import com.fitted.cart.dto.CartRequest;
import com.fitted.cart.dto.CartResponse;
import com.fitted.cart.dto.CreateCartResponse;
import com.fitted.cartItem.CartItem;
import com.fitted.cartItem.CartItemRepository;
import com.fitted.product.Product;
import com.fitted.product.ProductRepository;
import com.fitted.cartItem.dto.CartItemResponse;
import com.fitted.productOption.ProductOption;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.user.User;
import com.fitted.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public void checkProductWithProductOption(Product product, ProductOption productOption) {
        if (!productOption.getProductId().equals(product.getId())) {
            throw new NoSuchElementException("상품과 옵션이 맞지 않습니다.");
        }
    }

    public CartItemResponse convertCartItemResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProductId(),
                cartItem.getProductOptionId(),
                cartItem.getProductCount()
        );
    }

    public CreateCartResponse convertCreateCartResponse(Cart cart, CartItemResponse cartItemResponse) {
        return new CreateCartResponse(
                cart.getId(),
                cartItemResponse
        );
    }

    @Transactional
    public CreateCartResponse create(String supabaseId, CartRequest request) {
        User user = userRepository.findBySupabaseId(supabaseId).orElseThrow(
                () -> new NoSuchElementException("해당하는 사용자가 없습니다."));
        Product product = productRepository.findById(request.productId()).orElseThrow(
                () -> new NoSuchElementException("해당하는 상품이 없습니다."));
        ProductOption productOption = productOptionRepository.findById(request.productOptionId()).orElseThrow(
                () -> new NoSuchElementException("해당하는 상품 옵션이 없습니다."));
        checkProductWithProductOption(product, productOption);
        Cart cart = cartRepository.save(new Cart(user.getId()));
        CartItem cartItem = cartItemRepository.save(new CartItem(
                request.productId(),
                request.productOptionId(),
                request.productCount(),
                cart.getId()
        ));
        CartItemResponse cartItemResponse = convertCartItemResponse(cartItem);
        return convertCreateCartResponse(cart, cartItemResponse);
    }

    public List<CartItemResponse> convertCartResponseList(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> new CartItemResponse(
                        cartItem.getId(),
                        cartItem.getProductId(),
                        cartItem.getProductOptionId(),
                        cartItem.getProductCount()
                ))
                .toList();
    }

    public CartResponse convertCartResponse(Cart cart, List<CartItemResponse> cartItemResponses) {
        return new CartResponse(
                cart.getId(),
                cartItemResponses
        );
    }

    public CartResponse findByCartId(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new NoSuchElementException("해당하는 장바구니가 없습니다."));
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cartId);
        List<CartItemResponse> cartItemResponses = convertCartResponseList(cartItems);
        return convertCartResponse(cart, cartItemResponses);
    }
}
