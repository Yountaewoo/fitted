package com.fitted.order;

import com.fitted.order.dto.OrderRequest;
import com.fitted.order.dto.OrderResponse;
import com.fitted.order.dto.OrderListResponse;
import com.fitted.order.dto.OrderSummaryResponse;
import com.fitted.orderDetail.OrderDetail;
import com.fitted.orderDetail.OrderDetailRepository;
import com.fitted.orderDetail.dto.OrderDetailRequest;
import com.fitted.orderDetail.dto.OrderDetailResponse;
import com.fitted.product.Product;
import com.fitted.product.ProductRepository;
import com.fitted.productOption.ProductOption;
import com.fitted.productOption.ProductOptionRepository;
import com.fitted.security.AuthorizationService;
import com.fitted.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final AuthorizationService authorizationService;

    private Order createOrder(User user, OrderRequest orderRequest) {
        return ordersRepository.save(new Order(
                user.getId(),
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

    private OrderDetail createOrderDetail(Order order, ProductOption productOption, OrderDetailRequest orderDetailRequest) {
        return orderDetailRepository.save(new OrderDetail(
                order.getId(),
                productOption.getProductId(),
                orderDetailRequest.productCount()
        ));
    }

    private List<OrderDetailResponse> mapOrderDetailsToResponseList(Order order) {
        return orderDetailRepository.findAllByOrderId(order.getId()).stream()
                .map(orderDetail -> new OrderDetailResponse(
                        orderDetail.getProductId(),
                        orderDetail.getProductCount()))
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order, List<OrderDetailResponse> orderDetailResponses) {
        return new OrderResponse(
                order.getId(),
                order.getAddress(),
                order.getTotalPrice(),
                orderDetailResponses
        );
    }

    private OrderSummaryResponse mapToOrderSummaryResponse(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getAddress(),
                order.getTotalPrice());
    }

    @Transactional
    public OrderResponse create(String supabaseId, OrderRequest orderRequest) {
        User user = authorizationService.checkCustomer(supabaseId);
        Order order = createOrder(user, orderRequest);
        for (OrderDetailRequest orderDetailRequest : orderRequest.requests()) {
            ProductOption productOption = getProductOptionById(orderDetailRequest);
            Product product = getProductByOption(productOption);
            createOrderDetail(order, productOption, orderDetailRequest);
            order.sumTotalPrice(orderDetailRequest.productCount() * product.getPrice());
        }
        List<OrderDetailResponse> orderDetailResponses = mapOrderDetailsToResponseList(order);
        return mapToOrderResponse(order, orderDetailResponses);
    }

    public OrderListResponse getAll(String supabaseId) {
        User user = authorizationService.checkCustomer(supabaseId);
        List<OrderSummaryResponse> summaries = ordersRepository
                .findAllByUserId(user.getId())
                .stream()
                .map(this::mapToOrderSummaryResponse)
                .toList();
        return new OrderListResponse(summaries);
    }

    public OrderResponse findByOrderId(String supabaseId, Long orderId) {
        User user = authorizationService.checkCustomer(supabaseId);
        Order order = ordersRepository.findById(orderId).orElseThrow(
                () -> new NoSuchElementException("해당하는 주문이 없습니다."));
        if (!order.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("주문한 사용자와 일치하지 않습니다.");
        }
        List<OrderDetailResponse> orderDetailResponses = mapOrderDetailsToResponseList(order);
        return mapToOrderResponse(order, orderDetailResponses);
    }
}
