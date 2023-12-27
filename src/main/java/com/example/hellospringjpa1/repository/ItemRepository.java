package com.example.hellospringjpa1.repository;

import com.example.hellospringjpa1.domain.item.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            // 완전히 새로 생성한 객체
            em.persist(item);
        } else {
            // 이미 디비에 등록된 객체
            // em.merge : 업데이트와 비슷하다
            em.merge(item);
        }
    }

    public Item findById(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("SELECT I FROM Item I", Item.class)
                .getResultList();
    }
}
