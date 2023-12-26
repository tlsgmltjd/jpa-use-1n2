package com.example.hellospringjpa1.domain;

import com.example.hellospringjpa1.domain.item.Item;
import jakarta.persistence.*;
import lombok.Data;

import static jakarta.persistence.FetchType.*;

@Entity
@Data
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer orderPrice; // 주문가격

    private Integer count; // 주문수량

}
