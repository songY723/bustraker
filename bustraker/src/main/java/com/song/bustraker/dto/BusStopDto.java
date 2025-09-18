package com.song.bustraker.dto;


public class BusStopDto {
    private String stopId;   // 정류장 ID
    private String stopName; // 정류장 이름
    private String gpsLati;  // 위도
    private String gpsLong;  // 경도
    private int seq;
    private int udType;
    private int dir; // 0=상행, 1=하행 ?
    
    // Getter & Setter
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

    public String getGpsLati() {
        return gpsLati;
    }

    public void setGpsLati(String gpsLati) {
        this.gpsLati = gpsLati;
    }

    public String getGpsLong() {
        return gpsLong;
    }

    public void setGpsLong(String gpsLong) {
        this.gpsLong = gpsLong;
    }

    public int getSeq() { return seq; }
    public void setSeq(int seq) { this.seq = seq; }

    public int getUdType() { return udType; }
    public void setUdType(int udType) { this.udType = udType; }

    public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	@Override
	public String toString() {
	    return "BusStopDto{" +
	            "stopId='" + stopId + '\'' +
	            ", stopName='" + stopName + '\'' +
	            ", gpsLati='" + gpsLati + '\'' +
	            ", gpsLong='" + gpsLong + '\'' +
	            ", seq=" + seq +
	            ", udType=" + udType +
	            ", dir=" + dir +
	            '}';
	}
}