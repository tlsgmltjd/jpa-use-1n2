package com.example.hellospringjpa1.api;

import com.example.hellospringjpa1.domain.Address;
import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.domain.OrderStatus;
import com.example.hellospringjpa1.repository.OrderRepository;
import com.example.hellospringjpa1.repository.OrderSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * xToOne (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    // V1 엔티티를 직접 노출
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // 지연로딩이 걸린 엔티티와 연관관계인 엔티티를 그대로 반환시키면 프록시 객체를 파싱할 수 없어서 에러가 발생함
        // -> Hibernate5JakartaModule을 빈으로 등록시켜서 프록시 객체를 null로 두고 파싱할 수 있음, 속성을 주어 지연로딩 엔티티를 조회해올 수 있다.
        // 하지만 이렇게 엔티티를 노출하는 것은 하면 안되는 짓이다..
        return orderRepository.findAllByString(new OrderSearch());
    }

    // V2 엔티티를 DTO로 변환
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orders2V2() {
        // N+1 문제 발생
        // 첫 쿼리 후 해당 쿼리로 인한 Lazy 로딩 추가 쿼리 N번이 발생
        // 그렇다고 EAGER로 설정하면 쿼리를 예측할 수 없고 성능도 안나온다.
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
    }

    // V3 페치조인으로 최적화
    // 필요한, 연관관계의 객체그래프를 SQL 한방에 가져온다.
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orders2V3() {
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // LAZY 초기화 +쿼리
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress(); // LAZY 초기화 +쿼리
        }
    }
}
