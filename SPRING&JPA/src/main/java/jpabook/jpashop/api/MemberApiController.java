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
import java.util.List;
import java.util.stream.Collectors;

@RestController //resppnseBody + controller
@RequiredArgsConstructor
public class MemberApiController {
    
    private final MemberService memberService;

    //array자체를 바로 반환하면 api스펙의 확장성이 떨어진다.
    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberVs(){
        List<Member> findMembers = memberService.findMembers();
        int count = findMembers.size();
        List<MemberDTO> collect = findMembers.stream()
                .map(m -> new MemberDTO(m.getId(), m.getName()))
                .collect(Collectors.toList());
        return new Result(collect, count);
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
        private int count;
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO{
        private Long id;
        private String name;
    }
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
