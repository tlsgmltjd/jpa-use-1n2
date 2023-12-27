package com.example.hellospringjpa1.service;

import com.example.hellospringjpa1.domain.Member;
import com.example.hellospringjpa1.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 테스트가 끝나면 다 롤백됨
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired  MemberRepository memberRepository;
//    @Autowired EntityManager em;


    @Test
//    @Rollback(value = false)
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("신희성");

        // when
        Long savedId = memberService.join(member);

        // then
//        em.flush(); // 영속성 컨텍스트 안의 객체를 디비로 insert 쿼리를 날림
        assertEquals(member, memberRepository.findById(savedId));

        // GenerateValue 전략에는 persist시 insert문이 안나감(커밋 -> 플러쉬 -> 영속성 컨텍스트의 객체가 디비의 인서트됨)
        // 전체 테스트 클래스에 트랜잭셔널 어노테이션 때문에 테스트 메서드가 끝날 시 커밋이 아니라 롤백됨
        // @Rollback(value = false) 을 메서드에 달아줌으로써 insert 쿼리가 날라가는걸 볼 수 있다.

    }

    // expected 키워드는 junit4에 있음
//    @Test(expected = IllegalStateException.class)
    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("신희성");

        Member member2 = new Member();
        member2.setName("신희성");

        // when
        memberService.join(member1);

        // then
        // 이름 중복 회원 join으로 예외가 발생한다.
        assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));
    }
}