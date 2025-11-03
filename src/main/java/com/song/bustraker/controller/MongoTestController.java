package com.song.bustraker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.song.bustraker.dao.MemberDao;

@RestController
@RequestMapping("/api/test")
public class MongoTestController {

    @Autowired
    private MemberDao repo;

    @GetMapping("/ping")
    public String ping() {
        return "Mongo 연결 확인: " + repo.count() + "명 저장됨";
    }
}
