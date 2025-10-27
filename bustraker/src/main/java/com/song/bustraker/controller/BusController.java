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

import org.springframework.http.ResponseEntity;
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

    /**
     * 정류장 도착정보 조회
     * frontend에서 보낼 수 있는 파라미터 이름들을 모두 허용(arsId / busStopId / busNodeId)
     * busRouteId는 선택(필터) 파라미터 — 서비스에서 해당 노선만 필터링 하도록 사용.
     */
    @GetMapping("/api/arrivalByStop")
    public ResponseEntity<List<ArrivalInfoDto>> getArrivalByStop(
            @RequestParam(value = "arsId", required = false) String arsId,
            @RequestParam(value = "busStopId", required = false) String busStopId,
            @RequestParam(value = "busNodeId", required = false) String busNodeId,
            @RequestParam(value = "busRouteId", required = false) String busRouteId) {

        // 1) 어떤 파라미터로 들어왔는지 로그 출력 (디버깅 용)
        System.out.println("[DEBUG] /api/arrivalByStop called. arsId=" + arsId
                + ", busStopId=" + busStopId + ", busNodeId=" + busNodeId + ", busRouteId=" + busRouteId);

        // 2) 우선순위로 ID 선택: arsId(=5자리 stopId) 우선 → busStopId → busNodeId(=7자리)
        String effectiveId = (arsId != null && !arsId.isEmpty()) ? arsId
                : (busStopId != null && !busStopId.isEmpty()) ? busStopId
                : (busNodeId != null && !busNodeId.isEmpty()) ? busNodeId
                : null;

        if (effectiveId == null) {
            // 클라이언트가 보낸 파라미터가 없음 — 400 반환
            System.out.println("[WARN] /api/arrivalByStop missing any stop id");
            return ResponseEntity.badRequest().build();
        }

        // 3) 서비스 호출 (서비스 시그니처에 맞춰 호출)
        //    service.getArrivalInfoByStop(id, routeId)처럼 두번째 인자로 busRouteId 전달해서
        //    백엔드에서 이미 노선 필터링 하도록 구현하는게 바람직
        List<ArrivalInfoDto> arrivals = busArrivalService.getArrivalInfoByStop(effectiveId, busRouteId);

        System.out.println("[DEBUG] /api/arrivalByStop -> returned " + (arrivals == null ? 0 : arrivals.size()) + " items");

        return ResponseEntity.ok(arrivals);
    }

    // 노선 전체의 정류장별 도착정보 (백엔드에서 각 정류장별로 arrival 호출 후 합쳐서 반환)
    @GetMapping("/api/arrivalByRoute")
    public List<StationArrivalDto> getArrivalByRoute(@RequestParam String busRouteId) {
        return busArrivalService.getArrivalInfoByRoute(busRouteId);
    }
}

