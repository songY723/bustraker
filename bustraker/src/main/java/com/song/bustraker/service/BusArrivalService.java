package com.song.bustraker.service;

import com.song.bustraker.dto.ArrivalInfoDto;
import com.song.bustraker.dto.StationArrivalDto;

import java.util.List;

public interface BusArrivalService {
   

	List<StationArrivalDto> getArrivalInfoByRoute(String busRouteId);

	List<ArrivalInfoDto> getArrivalInfoByStop(String busStopId, String busRouteId);
}

