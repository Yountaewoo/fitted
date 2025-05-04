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

    public Order(Long userId, String address) {
        this.userId = userId;
        this.address = address;
    }

    public void sumTotalPrice(int price) {
        this.totalPrice = totalPrice + price;
    }
}
