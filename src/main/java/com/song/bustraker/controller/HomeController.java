package com.song.bustraker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Hello, Bus Tracker!";
    }

    @GetMapping("/test")
    public String test() {
        return "Controller is working!";
    }
}
