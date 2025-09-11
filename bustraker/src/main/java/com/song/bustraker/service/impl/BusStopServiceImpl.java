package com.song.bustraker.service.impl;

import com.song.bustraker.dto.BusStopDto;
import com.song.bustraker.service.BusStopService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusStopServiceImpl implements BusStopService {

	private static final String API_URL = "http://openapitraffic.daejeon.go.kr/api/rest/busRouteInfo/getStaionByRoute";

    @Value("${bus.api.key}")
    private String serviceKey;

    @Override
    public List<BusStopDto> getStationsByRoute(String busRouteId) throws Exception {
    	
    	
        // URI 생성 후 URL 변환 (Java 20 이상 권장)
    	String urlStr = API_URL + "?busRouteId=" + busRouteId + "&serviceKey=" + serviceKey;
    	URI uri = new URI(urlStr);  // URI 생성
    	URL url = uri.toURL(); 
    	
        try (InputStream in = url.openStream()) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(in);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("itemList");
            List<BusStopDto> stationList = new ArrayList<>();

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    BusStopDto stop = new BusStopDto();
                    stop.setStopId(getTagValue("BUS_STOP_ID", element));
                    stop.setStopName(getTagValue("BUSSTOP_NM", element));
                    stop.setGpsLati(getTagValue("GPS_LATI", element));
                    stop.setGpsLong(getTagValue("GPS_LONG", element));
                    stationList.add(stop);
                }
            }
            return stationList;
        }
    }
    

    private static String getTagValue(String tag, Element element) {
        NodeList nlList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = nlList.item(0);
        return (nValue == null) ? null : nValue.getNodeValue();
    }
}

