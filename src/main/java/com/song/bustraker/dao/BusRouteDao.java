package com.song.bustraker.dao;

import java.util.List;

import com.song.bustraker.dto.BusRouteDto;

public interface BusRouteDao {
    List<BusRouteDto> fetchAllRoutes();
    BusRouteDto findById(String routeId);
}
