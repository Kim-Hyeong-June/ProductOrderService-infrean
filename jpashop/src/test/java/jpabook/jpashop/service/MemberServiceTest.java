package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("킴");
        //when
        Long saveId = memberService.join(member);

        //then
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test
    void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");
        //when
        Member member2 = new Member();
        member2.setName("kim");

        memberService.join(member1);

        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }

    @Test
    void 회원_2명이상_체크() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim2");
        //when
        memberService.join(member1);
        memberService.join(member2);
        //then
    }
}
