package com.example.hellospringjpa1.repository.order.simplerquery;

import com.example.hellospringjpa1.domain.Address;
import com.example.hellospringjpa1.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// DTO 객체는 엔티티를 의존해도 괜찮다. 한번 쓰고 버려질 객체여서 크게 신경 쓰지 않아도 됌
@Getter
@AllArgsConstructor
public class SimpleOrderQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
}
