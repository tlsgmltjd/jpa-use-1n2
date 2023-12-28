package com.example.hellospringjpa1.service;

import com.example.hellospringjpa1.domain.Delivery;
import com.example.hellospringjpa1.domain.Member;
import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.domain.OrderItem;
import com.example.hellospringjpa1.domain.item.Item;
import com.example.hellospringjpa1.repository.ItemRepository;
import com.example.hellospringjpa1.repository.MemberRepository;
import com.example.hellospringjpa1.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findById(memberId);
        Item item = itemRepository.findById(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        return order.getId();

        // OrderItem, Delivery는 casescade가 걸려있어서 Order가 persist될 때 같이 persist된다.
        // 그래서 Order만 persist해도됨
    }

    /**
     * 취소
     */
    @Transactional
    public void cancelOrder(Long id) {
        // 주문 엔티티 조회
        Order order = orderRepository.findById(id);

        // 주문 취소
        order.cancel();

        // 영속성 컨텍스트 안에서 엔티티 안에서 데이터 변경이 일어나면
        // 더티 체킹, 변경 내역 감지가 일어나면서 변경된 내역을들 다 변경하여 update 쿼리가 날라간다.
    }

    /*
    * 검색
    * */
//    public List<Order> findORders(OrederSearch orederSearch) {
//        return orderRepository.findAll(orderRepository);
//    }
}
