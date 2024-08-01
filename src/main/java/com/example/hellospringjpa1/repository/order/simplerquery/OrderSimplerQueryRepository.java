package com.example.hellospringjpa1.repository.order.simplerquery;

import com.example.hellospringjpa1.domain.Order;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimplerQueryRepository {

    private final EntityManager em;

    // 이렇게 화면, API 스펙에 의존성이 있는 쿼리 메서드는 repository 계층이 아닌 따로 빼는것이 이상적
    // repository는 가급적 순수한 엔티티를 조회하기 위해 쓰인다. 재사용성.
    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new com.example.hellospringjpa1.repository.order.simplerquery.SimpleOrderQueryDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address" +
                        ") from Order o "+
                        "join o.member m " +
                        "join o.delivery d", SimpleOrderQueryDto.class
        ).getResultList();
    }
}
