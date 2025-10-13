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

    // 도착 정보 API (정류장별)
    private static final String ARRIVAL_API_URL = "http://openapitraffic.daejeon.go.kr/api/rest/arrive/getArrInfoByStopID";
    // 노선별 정류장 목록 API
    private static final String ROUTE_STATION_API_URL = "http://openapitraffic.daejeon.go.kr/api/rest/arrive/getRouteStationList";

    @Override
    public List<ArrivalInfoDto> getArrivalInfoByStop(String busNodeId) {
        List<ArrivalInfoDto> list = new ArrayList<>();
        try {
            String urlStr = ARRIVAL_API_URL + "?serviceKey=" + serviceKey + "&BusNodeID=" + busNodeId;
         
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
           

            Document doc = parseXml(responseXml);
            NodeList nList = doc.getElementsByTagName("itemList");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    ArrivalInfoDto dto = new ArrivalInfoDto();
                    dto.setBusStopId(getTagValue("BUS_STOP_ID", el));
                    dto.setRouteNo(getTagValue("ROUTE_NO", el));
                    dto.setExtimeMin(parseInt(getTagValue("EXTIME_MIN", el)));
                    dto.setExtimeSec(parseInt(getTagValue("EXTIME_SEC", el)));
                    dto.setArrTime(getTagValue("INFO_OFFER_TM", el));
                    list.add(dto);
                }
            }

        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<StationArrivalDto> getArrivalInfoByRoute(String busRouteId) {
        List<StationArrivalDto> result = new ArrayList<>();
        try {
            String url = ROUTE_STATION_API_URL + "?serviceKey=" + serviceKey + "&busRouteId=" + busRouteId;
            String responseXml = restTemplate.getForObject(url, String.class);

            Document doc = parseXml(responseXml);
            NodeList nList = doc.getElementsByTagName("itemList");

            //System.out.println("[DEBUG] getArrivalInfoByRoute(" + busRouteId + "): 총 " + nList.getLength() + " 정류장");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;

                    // 🚍 정류장 정보 추출
                    String stopId = getTagValue("BUSSTOP_ID", el); // 5자리 (참고용)
                    String stopName = getTagValue("BUSSTOP_NM", el);
                    String nodeId = getTagValue("BUS_NODE_ID", el); // 7자리 (실사용)

                    StationArrivalDto dto = new StationArrivalDto();
                    dto.setStopId(stopId);
                    dto.setStopName(stopName);
                    dto.setNodeId(nodeId);

                    // ⚡ 각 정류장 도착정보 조회 (BUS_NODE_ID 기준)
                    List<ArrivalInfoDto> arrivals = getArrivalInfoByStop(nodeId);
                    dto.setArrivals(arrivals);

                    result.add(dto);

                   // System.out.println("[DEBUG] 정류장: " + stopName + " (" + nodeId + "), 도착정보: " + arrivals.size());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) return null;
        Node node = nodeList.item(0);
        if (node == null || node.getFirstChild() == null) return null;
        return node.getFirstChild().getNodeValue().trim();
    }

    private static int parseInt(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return 0;
        }
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


