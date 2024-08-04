package com.example.hellospringjpa1.repository;

import com.example.hellospringjpa1.domain.Order;
import com.example.hellospringjpa1.repository.order.simplerquery.SimpleOrderQueryDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findById(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {

        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<Order> findAllWithItem() {

        // 1:다 조인시에는 다쪽이 기준으로 데이터를 조회한다.
        // 그렇게 된다면 1쪽의 데이터가 중복값이 출력된다.
        // 아래의 상황으로 보면 Order엔티티 List를 조회하지만 1:다, 컬렉션 연관관계의 객체그래프를 페치조인한다.
        // 그렇게 된다면 데이터베이스 결과 row에서는 1쪽인 Order의 결과과 중복되어 출력된다.
        // 그래서 중복된 Order의 갯수만큰 List를 만들어 반환된다.

        // 이런 중복 문제를 막기 위해 distinct 키워드 사용할 수 있다.
        // distinct 키워드를 붙히면 디비 쿼리에도 붙혀주고 애플리케이션 단에서 JPA가 중복된 데이터를 제거하여 반환해준다.
        // 하이버네이트 6 버전부터 기본적으로 distinct가 적용되어 중복 문제는 발생하지 않는다.

        // TODO 1:다, 컬렉션 연관관계는 페이징이 불가하다.
        // WARNNING : HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
        // 컬렉션 연관관계에 대해 페이징을 적용하려 한다면 "메모리에 모든 데이터를 퍼올려서 페이징을 적용"한다. <- 페이징에 대한 의미가 없고 성능이 저하된다.
        // 위에서 말한 1:다 연관관계에 대한 데이터 중복 문제 때문이다.

        // + 컬렉션 페이조인은 두개이상 사용하면 안된다. 데이터 부정합을 초래할 수 있음

        return em.createQuery(
                        "select o from Order o " +
                                "join fetch o.member " +
                                "join fetch o.delivery " +
                                "join fetch o.orderItems oi " +
                                "join fetch oi.item i", Order.class)
                .setMaxResults(10)
                .setFirstResult(0)
                .getResultList();
    }

    // XToOne 관계는 아무리 페치조인 해도 상관없다.
    // 컬렉션은 지연로딩으로 조회하는것이 좋다.

    // 지연로딩 성능 최적화를 위해 batch fetch size를 적용하여 컬렉션이나 프록시 객체를 설정값만큼 한번에 조회해온다.
    // default_batch_fetch_size: 100, @BatchSize
    // 1 + N -> 1 + 1
    // 컬렉션 페치조인과 배치사이즈 설정은 트레이드 오프가 있다. 데이터가 많다면 데이터 전송량에 대해 최적화가 된다. 중복데이터가 없기 때문이다.
    // 페치조인은 쿼리 호출수가 1번이여서 네트워크 전송이 빠르긴하다. 하지만 컬렉션 연관관계가 있을때 페이징을 적용한다면 배치사이즈 설정을 고려해야한다.

    // XToOne -> 페치조인, 나머지는 default_batch_fetch_size로 최적화
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    // DTO로 조회할 때는 new 오퍼레이션을 사용해서 원하는 컬럼만 조회, 바로 DTO 객체 생성 가능하고 페치조인을 하면 안된다.
    // 연관된 엔티티, 객체 그래프를 가져오는 것이 아니라 연관된 엔티티의 값을 가져오는 것이기 때문에 일반 이너조인으로 해결 가능하다.
    // -> go to order.simplequery

    /*
    * Jpa Criteria
    * */
//    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
//        // ...
//    }

}
