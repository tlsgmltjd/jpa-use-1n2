package com.example.hellospringjpa1.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {

        // XToOne 관계를 먼저 조인 후 XToMany 관계를 각각 루프를 돌며 조회했다.
        // XToOne은 조인해도 row수가 증가하지 않고 XToMany은 조인하면 row수가 증가하기 때문이다.
        // -> 디비는 N쪽을 기준으로 데이터를 반환하기 때문이다.
        List<OrderQueryDto> result = findOrders();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        // N+1문제가 발생하던것을 루트1, 컬렉션1 = 쿼리 2번으로 최적화
        // XToOne관계를 먼저 조회 후 반환값의 PK로 컬렉션 관계를 in query로 한번에 조회
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        """
                                    select new com.example.hellospringjpa1.repository.order.query
                                    .OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) 
                                    from OrderItem oi 
                                    join oi.item i 
                                    where oi.order.id in :orderIds
                                """, OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderIemMap = orderItems.stream()
                .collect(groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderIemMap;
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream().map(OrderQueryDto::getOrderId).collect(toList());
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        // * oi.order.id 이렇게 select 절에 넣으면 OrderItem 테이블 컬럼에 FK가 있으므로 따로 Order의 참조가 일어나지 않고 FK값을 쓴다.
        return em.createQuery(
                        """
                                    select new com.example.hellospringjpa1.repository.order.query
                                    .OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) 
                                    from OrderItem oi 
                                    join oi.item i 
                                    where oi.order.id = :orderId
                                """, OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        """
                                    select new com.example.hellospringjpa1.repository.order.query
                                    .OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) 
                                    from Order o 
                                    join o.member m 
                                    join o.delivery d
                                """, OrderQueryDto.class
        ).getResultList();
    }
}
