package com.example.hellospringjpa1.service.query;

import com.example.hellospringjpa1.api.OrderApiController;
import com.example.hellospringjpa1.domain.Address;
import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.domain.OrderStatus;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.name = order.getMember().getName();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.address = order.getDelivery().getAddress();
        // lazy 초기화를 안해주면 프록시 객체기 때문에 직렬화를 할 수가 없음(error) 그래도 지금은 하이버네이트 모듈 빈등록해둬서 null값으로 나옴
        // 하지만 엔티티를 이렇게 노출하는것도 안된다 DTO로 다 바꾸자
//            order.getOrderItems().forEach(oi -> oi.getItem().getName());
//            this.orderItems = order.getOrderItems();

        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new).collect(toList());
    }
}
