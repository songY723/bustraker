package com.song.bustraker.controller;

import com.song.bustraker.dto.BusPosDto;
import com.song.bustraker.service.BusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping("/bus")
    public List<BusPosDto> getBusPositions(@RequestParam String busRouteId) throws Exception {
        return busService.getBusPositions(busRouteId);
    }
}
