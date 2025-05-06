package com.fitted.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String address;

    private int totalPrice;

    private OrderStatus orderStatus = OrderStatus.CREATED;

    public Order(Long userId, String address, int totalPrice) {
        this.userId = userId;
        this.address = address;
        this.totalPrice = totalPrice;
    }

    public Order(Long userId, String address) {
        this.userId = userId;
        this.address = address;
    }

    public void sumTotalPrice(int price) {
        this.totalPrice = totalPrice + price;
    }

    public void changeOrderStatusToPaid() {
        this.orderStatus = OrderStatus.PAID;
    }
}
