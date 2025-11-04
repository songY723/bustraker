package com.song.bustraker.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.song.bustraker.dto.ArrivalInfoDto;
import com.song.bustraker.dto.StationArrivalDto;
import com.song.bustraker.service.BusArrivalService;

@Service
public class BusArrivalServiceImpl implements BusArrivalService {

    @Value("${bus.api.key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // 도착 정보 API (정류장별, arsId 사용)
    private static final String ARRIVAL_API_URL = "https://openapitraffic.daejeon.go.kr/api/rest/arrive/getArrInfoByUid";
    // 노선별 정류장 목록 API
    private static final String ROUTE_STATION_API_URL = "https://openapitraffic.daejeon.go.kr/api/rest/arrive/getRouteStationList";

    // 📌 정류장별 도착정보 조회 (5자리 arsId 사용)
    @Override
    public List<ArrivalInfoDto> getArrivalInfoByStop(String busStopId, String busRouteId) {
        List<ArrivalInfoDto> list = new ArrayList<>();
        try {
            String urlStr = ARRIVAL_API_URL + "?arsId=" + busStopId + "&serviceKey=" + serviceKey;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/xml");

            InputStream inputStream = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)
                    ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            conn.disconnect();

            Document doc = parseXml(sb.toString());
            NodeList nList = doc.getElementsByTagName("itemList");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    String routeId = getTagValue("ROUTE_CD", el); // 노선 코드로 필터링

                    if (busRouteId.equals(routeId)) {
                        ArrivalInfoDto dto = new ArrivalInfoDto();
                        dto.setBusStopId(getTagValue("BUS_STOP_ID", el));
                        dto.setBusRouteId(routeId);
                        dto.setRouteNo(getTagValue("ROUTE_NO", el));
                        
                        // ⬅ 여기서 안전하게 분 단위 변환
                        int extimeMin = parseIntSafe(getTagValue("EXTIME_MIN", el));
                        if (extimeMin <= 0) {
                            int extimeSec = parseIntSafe(getTagValue("EXTIME_SEC", el));
                            extimeMin = (extimeSec / 60) + 1;  // 나머지는 버리고 +1분 추가
                        }
                        dto.setExtimeMin(extimeMin);
                        
                        dto.setDestination(getTagValue("DESTINATION", el));
                       // dto.setExtimeMin(parseIntSafe(getTagValue("EXTIME_MIN", el)));
                        //dto.setExtimeSec(parseIntSafe(getTagValue("EXTIME_SEC", el)));
                        dto.setInfoOfferTm(getTagValue("INFO_OFFER_TM", el));

                        list.add(dto);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 📌 노선별 정류장 정보 + 각 정류장 도착정보 조회
    @Override
    public List<StationArrivalDto> getArrivalInfoByRoute(String busRouteId) {
        List<StationArrivalDto> result = new ArrayList<>();
        try {
            String url = ROUTE_STATION_API_URL + "?serviceKey=" + serviceKey + "&busRouteId=" + busRouteId;
            String responseXml = restTemplate.getForObject(url, String.class);
            Document doc = parseXml(responseXml);
            NodeList nList = doc.getElementsByTagName("itemList");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;

                Element el = (Element) node;

                // ✅ 5자리 arsId (BUS_STOP_ID) 사용
                String stopId = getTagValue("BUS_STOP_ID", el);
                String stopName = getTagValue("BUSSTOP_NM", el);

                StationArrivalDto dto = new StationArrivalDto();
                dto.setStopId(stopId);
                dto.setStopName(stopName);

                // ✅ 도착 정보 조회 시 stopId (BUS_STOP_ID)를 사용해야 함!
                List<ArrivalInfoDto> arrivals = getArrivalInfoByStop(stopId, busRouteId);
                dto.setArrivals(arrivals);

                result.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private int parseIntSafe(String val) {
        try {
            return val == null ? 0 : Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) return null;
        Node node = nodeList.item(0);
        if (node == null || node.getFirstChild() == null) return null;
        return node.getFirstChild().getNodeValue().trim();
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        doc.getDocumentElement().normalize();
        return doc;
    }
}



