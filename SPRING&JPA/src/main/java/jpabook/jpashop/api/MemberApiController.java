package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController //resppnseBody + controller
@RequiredArgsConstructor
public class MemberApiController {
    
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        //json데이터를 member형태로 변환
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid CreateMemberRequest request){
        //json데이터를 DTO형태로 변환
        Member member = new Member();
        member.setName(request.name);
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberResponse(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest updateMemberRequest){
        memberService.update(id, updateMemberRequest.name);
        Member findMember= memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        @NotEmpty
        private Long id;
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class CreateMemberResponse{
        private Long id;
    }
}
