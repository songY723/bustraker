package com.song.bustraker.service;

import com.song.bustraker.dto.BusPosDto;
import java.util.List;

public interface BusPosService {
    List<BusPosDto> getBusPositions(String busRouteId) throws Exception;
}
