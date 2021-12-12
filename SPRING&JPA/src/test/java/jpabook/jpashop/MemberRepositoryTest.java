package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    //test에 transactional 어노테이션이 있으면 테스트 완료 후 자동 롤백
    @Test
    @Transactional
//    @Rollback(value = false)
    public void testMember() throws Exception{
//        Member member = new Member();
//        member.setUsername("memberA");
//
//        Long savedId = memberRepository.save(member);
//        Member findMember = memberRepository.find(savedId);
//
//        Assertions.assertThat(member.getId()).isEqualTo(findMember.getId());
//        Assertions.assertThat(member.getUsername()).isEqualTo(findMember.getUsername());
//
//        Assertions.assertThat(findMember).isEqualTo(member);
    }
}