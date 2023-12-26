package com.example.hellospringjpa1.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // 내장타입
    private Address address;

    @OneToMany(mappedBy = "member") // 하나의 회원이 여러개의 Order를 갖는다.
    private List<Order> orders = new ArrayList<>();
}
