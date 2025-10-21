package com.song.bustraker.controller;

import com.song.bustraker.dto.ArrivalInfoDto;
import com.song.bustraker.dto.BusPosDto;
import com.song.bustraker.dto.BusRouteDto;
import com.song.bustraker.dto.BusStopDto;
import com.song.bustraker.dto.StationArrivalDto;
import com.song.bustraker.service.BusArrivalService;
import com.song.bustraker.service.BusPosService;
import com.song.bustraker.service.BusRouteService;
import com.song.bustraker.service.BusStopService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BusController {

    private final BusRouteService busRouteService;
    private final BusStopService busStopService;
    private final BusPosService busPosService;
    private final BusArrivalService busArrivalService;
    
    public BusController(BusRouteService busRouteService,
                         BusStopService busStopService,
                         BusPosService busPosService,
                         BusArrivalService busArrivalService) {
        this.busRouteService = busRouteService;
        this.busStopService = busStopService;
        this.busPosService = busPosService;
        this.busArrivalService = busArrivalService;
    }

    // 1️⃣ 전체 노선 목록 조회 
    @GetMapping("/api/routes")
    public List<BusRouteDto> getRoutesJson() {
        return busRouteService.getAllRoutes();
    }

    // 2️⃣ 특정 노선의 정류장 목록 조회
    @GetMapping("/api/stations")
    public List<BusStopDto> getStationsJson(@RequestParam String busRouteId) {
        return busStopService.getStationsByRoute(busRouteId);
    }

    // 3️⃣ 특정 노선 버스 실시간 위치 조회
    @GetMapping("/api/busPositions")  // 이름 통일 (JS쪽과 동일)
    public List<BusPosDto> getBusPositions(@RequestParam String busRouteId) throws Exception {
        return busPosService.getBusPositions(busRouteId);
    }

    // 4️⃣ 특정 정류장의 도착정보 조회 (BUS_NODE_ID 기준)
    @GetMapping("/api/arrivalByStop")
    public List<ArrivalInfoDto> getArrivalByStop(
            @RequestParam String busStopId,
            @RequestParam String busRouteId) {
        return busArrivalService.getArrivalInfoByStop(busStopId, busRouteId);
    }

    // 5️⃣ 노선 전체의 정류장별 도착정보 조회
    @GetMapping("/api/arrivalByRoute")
    public List<StationArrivalDto> getArrivalByRoute(@RequestParam String busRouteId) {
        return busArrivalService.getArrivalInfoByRoute(busRouteId);
    }
}

