package com.song.bustraker.controller;

import com.song.bustraker.dto.BusPosDto;
import com.song.bustraker.dto.BusStopDto;
import com.song.bustraker.service.BusPosService;
import com.song.bustraker.service.BusStopService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BusController {

    private final BusStopService busStopService;
    private final BusPosService busPosService;

    public BusController(BusStopService busStopService, BusPosService busPosService) {
        this.busStopService = busStopService;
        this.busPosService = busPosService;
    }

    // 1️⃣ 정류장 목록 조회 (웹페이지용)
    @GetMapping("/stations")
    public String getStations(@RequestParam(defaultValue = "30300001") String busRouteId, Model model) throws Exception {
        List<BusStopDto> stations = busStopService.getStationsByRoute(busRouteId);
        model.addAttribute("stations", stations);
        return "stations"; // JSP 파일명: stations.jsp 
    }

    // 2️⃣ 특정 노선 버스 실시간 위치 조회 (JSON 반환)
    @GetMapping("/bus")
    public List<BusPosDto> getBusPositions(@RequestParam String busRouteId) throws Exception {
        return busPosService.getBusPositions(busRouteId);
    }

    // 3️⃣ 정류장 클릭 시 도착 정보 조회 (추후 확장용)
    @GetMapping("/arrival")
    public String getArrivalInfo(@RequestParam String stopId, Model model) throws Exception {
        // TODO: stopId 기반으로 버스 도착 정보 호출
        // model.addAttribute("arrivals", arrivalList);
        return "arrival"; // JSP 파일명: arrival.jsp
    }
}

