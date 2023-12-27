package com.example.hellospringjpa1.service;

import com.example.hellospringjpa1.domain.Member;
import com.example.hellospringjpa1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// javax, spring이 제공하는 트랜잭셔널 어노테이션이 있다. 스프링이 제공하는 어노테이션을 사용하는 것이 더 좋다. (쓸 수 있는 옵션이 많다.)
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    // 생성자 인젝션 : 생성시 뭘 의존하고 있는지 명확하게 표현 가능, 테스트 코드 작성시에도 용의
    private MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> members = memberRepository.findByName(member.getName());
        if (!members.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }


    /**
     * 회원 단건 조회
     */
    public Member findById(Long id) {
        return memberRepository.findById(id);
    }
}
