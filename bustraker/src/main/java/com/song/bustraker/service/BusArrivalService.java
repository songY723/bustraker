package com.song.bustraker.service;

import com.song.bustraker.dto.ArrivalInfoDto;
import com.song.bustraker.dto.StationArrivalDto;

import java.util.List;

public interface BusArrivalService {
    List<ArrivalInfoDto> getArrivalInfoByStop(String busStopId);

	List<StationArrivalDto> getArrivalInfoByRoute(String busRouteId);
}

