package com.example.hellospringjpa1.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded // 내장타입
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member") // 하나의 회원이 여러개의 Order를 갖는다.
    private List<Order> orders = new ArrayList<>();
    // 컬렉션은 필드에서 바로 초기화 하는 것이 안전하다
    // null 문제에서 안전함, 하이버네이트는 영속화 할때 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경한다. 컬력션을 변경한다면 문제가 생김
}
