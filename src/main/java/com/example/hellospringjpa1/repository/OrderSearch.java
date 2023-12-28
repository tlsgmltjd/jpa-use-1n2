package com.example.hellospringjpa1.repository;

import com.example.hellospringjpa1.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    private String memberName; // 회원 이름
    private OrderStatus orderStatus; // 주문 상태
}
