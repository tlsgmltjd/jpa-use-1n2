package com.example.hellospringjpa1.api;

import com.example.hellospringjpa1.domain.Address;
import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.domain.OrderItem;
import com.example.hellospringjpa1.domain.OrderStatus;
import com.example.hellospringjpa1.repository.OrderRepository;
import com.example.hellospringjpa1.repository.OrderSearch;
import com.example.hellospringjpa1.repository.order.query.OrderQueryDto;
import com.example.hellospringjpa1.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

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

    // V2 DTO로 조회
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        return orderRepository.findAllByString(new OrderSearch())
                .stream().map(OrderDto::new)
                .collect(toList());
    }

    // V3 페치 조인 최적화
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        return orderRepository.findAllWithItem().stream().map(OrderDto::new)
                .collect(toList());
    }

    // V3.1 페이징 한계돌파
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_1(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                   @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return orderRepository.findAllWithMemberDelivery(offset, limit).stream().map(OrderDto::new)
                .collect(toList());
    }

    // V4 JPA에서 DTO로 조회
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @Getter
    static class OrderDto {

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

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
