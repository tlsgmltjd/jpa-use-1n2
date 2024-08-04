package com.example.hellospringjpa1.service.query;

import com.example.hellospringjpa1.api.OrderApiController;
import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.repository.OrderRepository;
import com.example.hellospringjpa1.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderQueryService {

    // OSIV - open session in view
    // OSIV는 요청이 들어올 때 영속성 컨텍스트를 생성하여 요청이 끝날때까지 영속성 컨텍스트를 유지하는 것이다.
    // 이 상태에서 트랜잭션을 시작한다면 열려있는 영속성 컨텍스트를 가져와서 사용한다. 트랜잭션이 끝나도 영속성 컨텍스트를 요청이 끝날때 까지 유지시킨다.
    // OSIV를 킨다면 트랜잭션 범위 밖에서도 영속성 컨텍스트에 대한 지연로딩이나 조회가 가능하다, 하지만 이렇게 된다면 요청의 라이프사이클 만큼 디비 커넥션을 물고 있게 된다.
    // 실시간 트래픽이 중요한 애플리케이션이라면 커넥션이 모자라는 일이 발생할 수도 있다.

    // OSIV를 끈다면 트랜잭션 범위만큼만 영속성 컨텍스트가 유지되고 트랜잭션 커밋 시점에 영속성 컨텍스트도 삭제되고 디비 커넥션도 반환한다.
    // 하지만 트랜잭션 범위 밖에서 지연로딩이 동작하지 않는다. OSIV를 끈 상태에서 복잡성을 관리하려면 Command와 Query를 분리하는 패턴이 있다.
    // 핵심 비즈니스로직은 생성, 수정정도의 작업이고 큰 무리가 없다. 하지만 조회 로직은 최적화가 중요하다
    // 크고 복잡한 애플리케이션이라면 둘의 관심사를 분리하는것이 유지보수에 좋다.

    // 핵심 비즈니스 로작과 API 스펙에 맞는 조회 서비스를 분리해 두는것이 좋다.

    private final OrderRepository orderRepository;

    public List<OrderDto> ordersV3() {
        return orderRepository.findAllWithItem().stream().map(OrderDto::new)
                .collect(toList());
    }

    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        return orders.stream().map(OrderDto::new)
                .collect(toList());
    }
}
