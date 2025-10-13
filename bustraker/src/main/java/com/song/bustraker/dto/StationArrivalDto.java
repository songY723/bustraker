package com.song.bustraker.dto;

import java.util.List;

//정류장 + 도착정보 묶음
public class StationArrivalDto {
 private String stopId;
 private String stopName;
 private String nodeId;
 private List<ArrivalInfoDto> arrivals;
 // getter/setter
 public String getStopId() {
	return stopId;
 }
 public void setStopId(String stopId) {
	this.stopId = stopId;
 }
 public String getStopName() {
	return stopName;
 }
 public void setStopName(String stopName) {
	this.stopName = stopName;
 }
 public List<ArrivalInfoDto> getArrivals() {
	return arrivals;
 }
 public void setArrivals(List<ArrivalInfoDto> arrivals) {
	this.arrivals = arrivals;
 }
 public String getNodeId() {
	return nodeId;
 }
 public void setNodeId(String nodeId) {
	this.nodeId = nodeId;
 }
}
