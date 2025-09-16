package com.song.bustraker.service;

import com.song.bustraker.dto.ArrivalInfoDto;

import java.util.List;

public interface BusArrivalService {
    List<ArrivalInfoDto> getArrivalInfoByStop(String busStopId);
}

