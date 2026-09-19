package com.sanchay.member;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanchay.exception.ResourceNotFoundException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member toggleMonthlyPaid(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found")
                );
    
        member.setMonthlyPaid(!member.isMonthlyPaid());
    
        return memberRepository.save(member);
    }

    public void markAllMonthlyPaid() {

        memberRepository.findAll()
                .forEach(member -> {
                    member.setMonthlyPaid(true);
                    memberRepository.save(member);
                });
    }
}