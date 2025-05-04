package com.fitted.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String address;

    private int totalPrice;

    public Orders(Long userId, String address) {
        this.userId = userId;
        this.address = address;
    }

    public void sumTotalPrice(int price) {
        this.totalPrice = totalPrice + price;
    }
}
