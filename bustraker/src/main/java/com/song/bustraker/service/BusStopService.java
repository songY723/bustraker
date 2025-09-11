package com.song.bustraker.service;

import com.song.bustraker.dto.BusStopDto;
import java.util.List;

public interface BusStopService {
    List<BusStopDto> getStationsByRoute(String busRouteId) throws Exception;
}
