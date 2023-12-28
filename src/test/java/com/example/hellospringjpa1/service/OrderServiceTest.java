package com.example.hellospringjpa1.service;

import com.example.hellospringjpa1.domain.Address;
import com.example.hellospringjpa1.domain.Member;
import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.domain.OrderStatus;
import com.example.hellospringjpa1.domain.item.Book;
import com.example.hellospringjpa1.domain.item.Item;
import com.example.hellospringjpa1.exception.NotEnoughStockException;
import com.example.hellospringjpa1.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    // 통합 테스트를 하는 이유 : JPA가 잘 엮여서 동작하는지 보는게 목적이다.
    // -> 더 좋은건 디비나 리포지토리의 의존성 없이 순수하게 해당 메서드만 단위 테스트 하는것이 좋다.

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        // given
        Member member = createMember();
        Book book = createBook("랩 레전드 JPA", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findById(orderId);

        // 주문 상태가 ORDER인가
        assertEquals(OrderStatus.ORDER, getOrder.getStatus());
        // 주문 상품의 종류의 수가 1개인가
        assertEquals(1, getOrder.getOrderItems().size());
        // 주문 가격은 가격 * 수량인가
        assertEquals(10000 * orderCount, getOrder.getTotalPrice());
        // 주문 수랑만큼 상품의 재고가 줄어들었나
        assertEquals(8, book.getStockQuantity());
    }

    @Test
    public void 상품주문_재고수량초과() throws NotEnoughStockException {
        // given
        Member member = createMember();
        Item item = createBook("랩 레전드 JPA", 10000, 10);

        int orderCount = 11;

        // when & then
        assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(), orderCount));
    }

    @Test
    public void 주문취소() throws Exception {
        // given
        Member member = createMember();
        Book book = createBook("랩 레전드 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order order = orderRepository.findById(orderId);

        assertEquals(OrderStatus.CANCEL, order.getStatus());
        assertEquals(10, book.getStockQuantity());

    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("신희성");
        member.setAddress(new Address("서울", "강가", "12345"));
        em.persist(member);
        return member;
    }
}