package com.example.hellospringjpa1.api;

import com.example.hellospringjpa1.domain.Member;
import com.example.hellospringjpa1.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    // API에 엔티티를 노출, 파라미터로 받는 행위는 하면 안된다.
    // 엔티티와 API 스펙이 1:1로 매핑되어버리기 때문에 엔티티 스펙이 변경되면 API 스펙도 변경된다.

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // API 스펙에 맞는 각각의 DTO 객체를 만들어서 받는것이 일반적이다.

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {
        // 커맨드성 메서드와 쿼리성 메서드를 분리함 CQS
        // 커맨드성 메서드인 엔티티를 수정하는 작업에서 수정된 엔티티를 반환하지 않는다.
        // -> 커멘드성 메서드에서 쿼리 기능도 하는 꼴이기 때문, 쿼리성 메서드를 따로 호출함 pk 조회는 성능저하가 심하지 않음
        // 이렇게 개발하는 것이 각각의 역할만 수행하고 수정과 읽기가 동시에 일어나지 않기에 읽기 편하고 성능개선, 유지보수성에 좋다.
        memberService.update(id, request.getName());
        Member findMember = memberService.findById(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotBlank
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
