package com.song.bustraker;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/routes")
    public String routesPage() {
        return "forward:/routes.html";
    }
    
    @GetMapping("/stations")
    public String stationsPage() {
        return "forward:/stations.html";
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "forward:/register.html";
    }
}
