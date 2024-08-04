package com.example.hellospringjpa1.service.query;

import com.example.hellospringjpa1.domain.OrderItem;
import lombok.Data;
import lombok.Getter;

@Data
public class OrderItemDto {

    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemDto(OrderItem orderItem) {
        this.itemName = orderItem.getItem().getName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
    }
}
