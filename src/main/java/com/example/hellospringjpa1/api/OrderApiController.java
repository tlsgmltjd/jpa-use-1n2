package com.example.hellospringjpa1.api;

import com.example.hellospringjpa1.domain.Address;
import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.domain.OrderItem;
import com.example.hellospringjpa1.domain.OrderStatus;
import com.example.hellospringjpa1.repository.OrderRepository;
import com.example.hellospringjpa1.repository.OrderSearch;
import com.example.hellospringjpa1.repository.order.query.OrderFlatDto;
import com.example.hellospringjpa1.repository.order.query.OrderItemQueryDto;
import com.example.hellospringjpa1.repository.order.query.OrderQueryDto;
import com.example.hellospringjpa1.repository.order.query.OrderQueryRepository;
import com.example.hellospringjpa1.service.query.OrderDto;
import com.example.hellospringjpa1.service.query.OrderQueryService;
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
    private final OrderQueryService orderQueryService;

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
        return orderQueryService.ordersV2();
    }

    // V3 페치 조인 최적화
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        return orderQueryService.ordersV3();
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

    // V5 JPA에서 DTO로 조회 - 컬렉션 조회 최적화
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    // V6 JPA에서 DTO로 조회 - 플랫 데이터 최적화
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        // 쿼리는 한번 나간다.
        // flat하게. 컬렉션 연관관계의 중복을 허용하면서 데이터를 받은 다음 애플리케이션 단에서 직접 파싱해서 사용하기 때문에 상황에 따라 위보다 느릴 수 있다.
        // 애플리케이션 추가작업이 많고 페이징이 불가하다 (중복을 허용하며 데이터를 가져왔기 때문)

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }
}
