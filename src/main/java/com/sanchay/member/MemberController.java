package com.sanchay.member;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @PatchMapping("/{id}/monthly-paid")
    public Member toggleMonthlyPaid(@PathVariable Long id) {
        return memberService.toggleMonthlyPaid(id);
    }

    @PatchMapping("/monthly-paid")
    public void markAllMonthlyPaid() {
        memberService.markAllMonthlyPaid();
    }
}