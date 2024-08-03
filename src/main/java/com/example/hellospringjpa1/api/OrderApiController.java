package com.example.hellospringjpa1.api;

import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.domain.OrderItem;
import com.example.hellospringjpa1.repository.OrderRepository;
import com.example.hellospringjpa1.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    // V1 엔티티 직접 노출 : 엔티티를 직접 노출하면 안됨
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        for (Order order : orders) {
            // lazy로딩 프록시 강제초기화
            order.getMember().getName();
            order.getDelivery().getId();
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                orderItem.getItem().getName();
            }
        }

        return orders;
    }

}
