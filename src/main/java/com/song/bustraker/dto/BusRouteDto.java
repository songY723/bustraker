package com.song.bustraker.dto;


public class BusRouteDto {
	  private String routeId;   // 노선 ID
	  private String routeName; // 노선 번호
	  private String routeType;
	  
	  public String getRouteId() {
		return routeId;
	  }
	  public void setRouteId(String routeId) {
		this.routeId = routeId;
	  }
	  public String getRouteName() {
		return routeName;
	  }
	  public void setRouteName(String routeName) {
		this.routeName = routeName;
	  }
	  public String getRouteType() {
		return routeType;
	  }
	  public void setRouteType(String routeType) {
		this.routeType = routeType;
	  }
	  
}
