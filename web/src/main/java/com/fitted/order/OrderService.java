package com.fitted.order;

import com.fitted.order.dto.OrderRequest;
import com.fitted.order.dto.OrderResponse;
import com.fitted.orderDetail.OrderDetail;
import com.fitted.orderDetail.OrderDetailRepository;
import com.fitted.orderDetail.dto.OrderDetailRequest;
import com.fitted.orderDetail.dto.OrderDetailResponse;
import com.fitted.product.Product;
import com.fitted.product.ProductRepository;
import com.fitted.productOption.ProductOption;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.security.AuthorizationService;
import com.fitted.user.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final AuthorizationService authorizationService;

    private Orders createOrder(Users users, OrderRequest orderRequest) {
        return orderRepository.save(new Orders(
                users.getId(),
                orderRequest.address()
        ));
    }

    private ProductOption getProductOptionById(OrderDetailRequest orderDetailRequest) {
        return productOptionRepository.findById(orderDetailRequest.productOptionId()).orElseThrow(
                () -> new NoSuchElementException(" 해당하는 상품 옵션이 없습니다."));
    }

    private Product getProductByOption(ProductOption productOption) {
        return productRepository.findById(productOption.getProductId()).orElseThrow(
                () -> new NoSuchElementException("해당하는 상품이 없습니다."));
    }

    private OrderDetail createOrderDetail(Orders orders, ProductOption productOption, OrderDetailRequest orderDetailRequest) {
        return orderDetailRepository.save(new OrderDetail(
                orders.getId(),
                productOption.getProductId(),
                orderDetailRequest.productCount()
        ));
    }

    private List<OrderDetailResponse> mapOrderDetailsToResponseList(Orders orders) {
        return orderDetailRepository.findAllByOrderId(orders.getId()).stream()
                .map(orderDetail -> new OrderDetailResponse(
                        orderDetail.getProductId(),
                        orderDetail.getProductCount()))
                .toList();
    }

    private OrderResponse mapToOrderResponse(Orders orders, List<OrderDetailResponse> orderDetailResponses) {
        return new OrderResponse(
                orders.getId(),
                orders.getAddress(),
                orders.getTotalPrice(),
                orderDetailResponses
        );
    }

    @Transactional
    public OrderResponse create(String supabaseId, OrderRequest orderRequest) {
        Users users = authorizationService.checkCustomer(supabaseId);
        Orders orders = createOrder(users, orderRequest);
        for (OrderDetailRequest orderDetailRequest : orderRequest.requests()) {
            ProductOption productOption = getProductOptionById(orderDetailRequest);
            Product product = getProductByOption(productOption);
            createOrderDetail(orders, productOption, orderDetailRequest);
            orders.sumTotalPrice(orderDetailRequest.productCount() * product.getPrice());
        }
        List<OrderDetailResponse> orderDetailResponses = mapOrderDetailsToResponseList(orders);
        return mapToOrderResponse(orders, orderDetailResponses);
    }
}
