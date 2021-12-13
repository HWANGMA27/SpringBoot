package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    //회원가입 테스트
    @Test
    public void addMemberTest() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);
        em.flush();

        //then
        Assertions.assertThat(member.getId()).isEqualTo(savedId);
        assertEquals(member, memberRepository.findOne(savedId));
    }

    //중복회원 예외처리 테스트
    @Test(expected = IllegalStateException.class)
    public void test() throws Exception{
        //given
        Member member1 = new Member();
        Member member2 = new Member();
        member1.setName("kim");
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);
    }
}