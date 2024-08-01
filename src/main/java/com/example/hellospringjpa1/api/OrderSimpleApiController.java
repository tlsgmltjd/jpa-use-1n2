package com.example.hellospringjpa1.api;

import com.example.hellospringjpa1.domain.Address;
import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.domain.OrderStatus;
import com.example.hellospringjpa1.repository.OrderRepository;
import com.example.hellospringjpa1.repository.OrderSearch;
import com.example.hellospringjpa1.repository.order.simplerquery.OrderSimplerQueryRepository;
import com.example.hellospringjpa1.repository.order.simplerquery.SimpleOrderQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

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
    private final OrderSimplerQueryRepository orderSimplerQueryRepository;

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

    // V4 JPA에서 DTO로 한번에 조회
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> orders2V4() {
        return orderSimplerQueryRepository.findOrderDtos();
    }

    // trade off v3 vs v4

    // V3
    // Order라는 엔티티를 조회한다는 것은 변하지 않은데 해당 엔티티와 연관관계인 엔티티의 객체 그래프를 한번에 조회해서 성능을 튜닝, 재사용성 높다

    // V4
    // 해당 API의 필요한 컬럼만 가져와서 필요한 필드만 가져와서 성능이 좋지만 (생각보다 미비하다), 재사용성이 떨어짐
    // 논리적인 계층을 허문다, 리포지토리 계층에서 API 스펙에 대한 의존성이 생겨버리는 꼴이다. -> 따로 계층을 만들어 처리하는 것도 방법 (ex. order.simplequery)

    // select 필드가 성능에 그렇게 영향을 미치지는 않는다. 데이터 사이즈가 너무 많다면 고민할만하다
    // 대부분의 성능은 인덱싱, 조인에서 발생한다.

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
