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

    // ✅ 노선별 정류장 목록 조회 API (정확한 endpoint)
    private static final String API_URL = "https://openapitraffic.daejeon.go.kr/api/rest/busRouteInfo/getStaionByRoute";

    @Override
    public List<BusStopDto> getStationsByRoute(String busRouteId) {
        List<BusStopDto> stationList = new ArrayList<>();
        try {
            String urlStr = API_URL + "?busRouteId=" + busRouteId + "&serviceKey=" + serviceKey;
            System.out.println("🚏 [정류장 요청 URL] " + urlStr);

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
            System.out.println("📩 [정류장 응답 XML] \n" + responseXml.substring(0, Math.min(500, responseXml.length())) + "...");
            // 너무 길면 앞부분만 출력

            stationList = parseStations(responseXml);

            System.out.println("✅ [정류장 파싱 완료] 총 " + stationList.size() + "개 정류장 불러옴");

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

                    // ✅ 여기서 정확히 태그 이름 일치시켜야 함
                    stop.setBusNodeId(getTagValue("BUS_NODE_ID", el)); // 7자리 내부용
                    stop.setStopId(getTagValue("BUS_STOP_ID", el));    // ✅ 5자리 arsId
                    stop.setStopName(getTagValue("BUSSTOP_NM", el));
                    stop.setGpsLati(getTagValue("GPS_LATI", el));
                    stop.setGpsLong(getTagValue("GPS_LONG", el));

                    String seqStr = getTagValue("BUSSTOP_SEQ", el);
                    stop.setSeq(parseIntOrDefault(seqStr, i + 1));

                    stations.add(stop);

                    // 📜 콘솔 출력
                    System.out.printf("  [%02d] %s (%s) - stopId=%s, nodeId=%s, seq=%s%n",
                            i + 1,
                            stop.getStopName(),
                            busStopIdSafe(stop),
                            stop.getStopId(),
                            stop.getBusNodeId(),
                            stop.getSeq()
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stations;
    }

    private static String busStopIdSafe(BusStopDto dto) {
        return (dto.getStopId() != null && !dto.getStopId().isEmpty()) ? dto.getStopId() : "❌ 없음";
    }

    // 안전한 숫자 변환
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

