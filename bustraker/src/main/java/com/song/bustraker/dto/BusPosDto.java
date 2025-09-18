package com.song.bustraker.dto;

public class BusPosDto {
    private String arrTime;
    private String busNodeId;
    private String busStopId;
    private int dir; 
    private String evtCd;
    private String gpsLati;
    private String gpsLong;
    private String plateNo;
    private String routeCd;
    private String streDt;
    private String totalDist;
    private int udType; 

    // getter & setter 전부 생성
    public String getArrTime() { return arrTime; }
    public void setArrTime(String arrTime) { this.arrTime = arrTime; }

    public String getBusNodeId() { return busNodeId; }
    public void setBusNodeId(String busNodeId) { this.busNodeId = busNodeId; }

    public String getBusStopId() { return busStopId; }
    public void setBusStopId(String busStopId) { this.busStopId = busStopId; }



    public String getEvtCd() { return evtCd; }
    public void setEvtCd(String evtCd) { this.evtCd = evtCd; }

    public String getGpsLati() { return gpsLati; }
    public void setGpsLati(String gpsLati) { this.gpsLati = gpsLati; }

    public String getGpsLong() { return gpsLong; }
    public void setGpsLong(String gpsLong) { this.gpsLong = gpsLong; }

    public String getPlateNo() { return plateNo; }
    public void setPlateNo(String plateNo) { this.plateNo = plateNo; }

    public String getRouteCd() { return routeCd; }
    public void setRouteCd(String routeCd) { this.routeCd = routeCd; }

    public String getStreDt() { return streDt; }
    public void setStreDt(String streDt) { this.streDt = streDt; }

    public String getTotalDist() { return totalDist; }
    public void setTotalDist(String totalDist) { this.totalDist = totalDist; }
	public int getDir() {
		return dir;
	}
	public void setDir(int dir) {
		this.dir = dir;
	}
	public int getUdType() {
		return udType;
	}
	public void setUdType(int udType) {
		this.udType = udType;
	}


}
