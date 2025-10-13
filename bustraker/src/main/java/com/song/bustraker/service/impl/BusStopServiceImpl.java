package com.song.bustraker.service.impl;

import com.song.bustraker.dto.BusStopDto;
import com.song.bustraker.service.BusStopService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusStopServiceImpl implements BusStopService {

    @Value("${bus.api.key}")
    private String serviceKey;

    private static final String API_URL = "http://openapitraffic.daejeon.go.kr/api/rest/busRouteInfo/getStaionByRoute";

    @Override
    public List<BusStopDto> getStationsByRoute(String busRouteId) {
        List<BusStopDto> stationList = new ArrayList<>();
        try {
            String urlStr = API_URL + "?busRouteId=" + busRouteId + "&serviceKey=" + serviceKey + "&reqPage=1";
           // System.out.println("요청 URL: " + urlStr);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/xml");

            InputStream inputStream = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)
                    ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            conn.disconnect();

            String responseXml = sb.toString();
            //System.out.println("응답 XML: " + responseXml);

            stationList = parseStations(responseXml);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stationList;
    }

    private List<BusStopDto> parseStations(String xml) {
        List<BusStopDto> stations = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("itemList");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    BusStopDto stop = new BusStopDto();

                    stop.setStopId(getTagValue("stopId", el));
                    stop.setStopName(getTagValue("BUSSTOP_NM", el));
                    stop.setGpsLati(getTagValue("GPS_LATI", el));
                    stop.setGpsLong(getTagValue("GPS_LONG", el));
                    stop.setBusNodeId(getTagValue("BUS_NODE_ID", el));
                    // 순서(SEQUENCE) 처리
                    stop.setSeq(parseIntOrDefault(getTagValue("SEQ", el), i + 1));

               
                    stations.add(stop);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stations;
    }

    // Null 빈 문자열을 기본값으로 안전하게 처리하는 메서드
    private int parseIntOrDefault(String value, int defaultVal) {
        if (value == null || value.isEmpty()) return defaultVal;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) return null;
        Node node = nodeList.item(0);
        if (node == null || node.getFirstChild() == null) return null;
        return node.getFirstChild().getNodeValue().trim();
    }
    
}

