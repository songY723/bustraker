package com.song.bustraker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.song.bustraker.dao.MemberDao;
import com.song.bustraker.dto.MemberDto;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberDao repo;

    // ✅ 회원가입 API
    @PostMapping("/register")
    public String register(@RequestBody MemberDto member) {
        if (repo.existsByUsername(member.getUsername())) {
            return "이미 존재하는 아이디입니다.";
        }

        repo.save(member);
        return "회원가입이 완료되었습니다.";
    }
}
