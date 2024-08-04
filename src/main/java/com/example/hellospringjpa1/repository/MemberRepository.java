package com.example.hellospringjpa1.repository;

import com.example.hellospringjpa1.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByName(String name); // spring data jpa가 인터페이스 메서드 시드니처를 보고 JPQL을 구현해준다.
}
